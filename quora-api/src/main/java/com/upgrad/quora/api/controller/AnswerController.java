package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.AnswerService;
import com.upgrad.quora.service.business.CommonService;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class AnswerController {

    @Autowired
    QuestionService questionService;

    @Autowired
    AnswerService answerService;

    @Autowired
    CommonService commonService;

    /**
     * This method creates answer
     *
     * @param answerRequest contains the details of answer to be stored in DB
     * @param accessToken Used for authorization
     * @throws AuthorizationFailedException When access token is invalid
     * @throws InvalidQuestionException when uuid of the question is invalid
     *
     * returns answer's uuid and message "ANSWER CREATED"
     */
    @RequestMapping(method = RequestMethod.POST, path = "/question/{questionId}/answer/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswer(final AnswerRequest answerRequest, @PathVariable("questionId") final String questionId, @RequestHeader("authorization") final String accessToken) throws InvalidQuestionException, AuthorizationFailedException {
        final QuestionEntity questionEntity = questionService.getQuestionByUuid(questionId);
        final UserAuthEntity userAuthEntity = commonService.authorizeAccessToken(accessToken);

        AnswerEntity answerEntity = new AnswerEntity();

        answerEntity.setUuid(UUID.randomUUID().toString());
        answerEntity.setAns(answerRequest.getAnswer());
        answerEntity.setDate(LocalDateTime.now());
        answerEntity.setUserId(userAuthEntity.getUser());
        answerEntity.setQuestionId(questionEntity);

        final AnswerEntity answerEntityCreated = answerService.createAnswer(answerEntity);
        final AnswerResponse answerResponse = new AnswerResponse().id(answerEntityCreated.getUuid()).status("ANSWER CREATED");

        return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.CREATED);
    }

    /**
     * This method updated the answer
     *
     * @param answerEditRequest contains the details of edited answer to be stored in DB
     * @param accessToken Used for authorization
     *
     * @throws AuthorizationFailedException When access token is invalid
     * @throws AnswerNotFoundException When answer uuid is invalid
     *
     * returns answer's uuid and message "ANSWER EDITED"
     */
    @RequestMapping(method = RequestMethod.PUT, path = "/answer/edit/{answerId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerEditResponse> editAnswerContent(final AnswerEditRequest answerEditRequest, @PathVariable("answerId") final String answerId, @RequestHeader("authorization") final String accessToken) throws AuthorizationFailedException, AnswerNotFoundException {
        final UserAuthEntity userAuthEntity = commonService.authorizeAccessToken(accessToken);
        final AnswerEntity answerEntity = answerService.getAnswerByUuid(answerId);

        final AnswerEntity editedQuestion = answerService.editAnswerByOwner(answerEditRequest.getContent(), answerEntity, userAuthEntity);
        final AnswerEditResponse answerEditResponse = new AnswerEditResponse().id(editedQuestion.getUuid()).status("ANSWER EDITED");

        return new ResponseEntity<AnswerEditResponse>(answerEditResponse, HttpStatus.OK);
    }

    /**
     * This method deletes answer
     *
     * @param answerId answer's uuid
     * @param accessToken Used for authorization
     *
     * @throws AuthorizationFailedException When access token is invalid
     * @throws AnswerNotFoundException When answer uuid is invalid
     *
     * returns answer's uuid and message "ANSWER DELETED"
     */
    @RequestMapping(method = RequestMethod.DELETE, path = "/answer/delete/{answerId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerDeleteResponse> deleteAnswer(@PathVariable("answerId") final String answerId, @RequestHeader("authorization") final String accessToken) throws AuthorizationFailedException, AnswerNotFoundException {
        final UserAuthEntity userAuthEntity = commonService.authorizeAccessToken(accessToken);
        final AnswerEntity answerEntity = answerService.getAnswerByUuid(answerId);

        final AnswerEntity deletedAnswer = answerService.deleteAnswerByUserOrAdmin(userAuthEntity, answerEntity);
        final AnswerDeleteResponse answerDeleteResponse = new AnswerDeleteResponse().id(deletedAnswer.getUuid()).status("ANSWER DELETED");

        return new ResponseEntity<AnswerDeleteResponse>(answerDeleteResponse, HttpStatus.OK);
    }
}

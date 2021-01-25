package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.CommonService;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/question")
public class QuestionController {

    @Autowired
    private CommonService commonService;

    @Autowired
    private QuestionService questionService;

    /**
     * This method returns all the answer to a question
     *
     * @param questionRequest contains the details of question to store in DB
     * @param accessToken Used for authorization
     *
     * @throws AuthorizationFailedException When access token is invalid
     *
     * returns question's uuid and message "QUESTION CREATED"
     */
    @RequestMapping(method = RequestMethod.POST, path = "/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(final QuestionRequest questionRequest, @RequestHeader("authorization") final String accessToken) throws AuthorizationFailedException {
        UserAuthEntity userAuthEntity = commonService.authorizeAccessToken(accessToken);

        QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setUuid(UUID.randomUUID().toString());
        questionEntity.setContent(questionRequest.getContent());
        questionEntity.setDate(LocalDateTime.now());
        questionEntity.setUserId(userAuthEntity.getUser());

        final QuestionEntity questionEntityCreated = questionService.createQuestion(questionEntity);
        final QuestionResponse questionResponse = new QuestionResponse().id(questionEntityCreated.getUuid()).status("QUESTION CREATED");

        return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.CREATED);
    }

    /**
     * This method returns all the questions
     *
     * @param accessToken Used for authorization
     * @throws AuthorizationFailedException When access token is invalid
     *
     * returns list of questions with uuid and content
     */
    @RequestMapping(method = RequestMethod.GET, path = "/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions(@RequestHeader("authorization") final String accessToken) throws AuthorizationFailedException {
        commonService.authorizeAccessToken(accessToken);

        final List<QuestionEntity> allQuestions = questionService.getAllQuestions();
        final List<QuestionDetailsResponse> questionDetailsResponses = convertQuestionListToJson(allQuestions);

        return new ResponseEntity<List<QuestionDetailsResponse>>(questionDetailsResponses, HttpStatus.OK);
    }

    /**
     * This method is used to edit a question content
     *
     * @param questionEditRequest contains the edited content of the question
     * @param accessToken Used for authorization
     *
     * @throws AuthorizationFailedException When access token is invalid
     * @throws InvalidQuestionException when uuid of the question is invalid
     *
     * returns questions's uuid and message "QUESTION EDITED"
     */
    @RequestMapping(method = RequestMethod.PUT, path = "/edit/{questionId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionEditResponse> editQuestionContent(final QuestionEditRequest questionEditRequest, @PathVariable("questionId") final String questionId, @RequestHeader("authorization") final String accessToken) throws AuthorizationFailedException, InvalidQuestionException {
        final UserAuthEntity userAuthEntity = commonService.authorizeAccessToken(accessToken);
        final QuestionEntity questionEntity = questionService.getQuestionByUuid(questionId);

        final QuestionEntity editedQuestion = questionService.editQuestionContentByOwner(questionEditRequest.getContent(), questionEntity, userAuthEntity);
        QuestionEditResponse questionEditResponse = new QuestionEditResponse().id(editedQuestion.getUuid()).status("QUESTION EDITED");

        return new ResponseEntity<QuestionEditResponse>(questionEditResponse, HttpStatus.OK);
    }

    /**
     * This method deletes a question
     *
     * @param questionId question's uuid
     * @param accessToken Used for authorization
     *
     * @throws AuthorizationFailedException When access token is invalid
     * @throws InvalidQuestionException when uuid of the question is invalid
     *
     * returns question's uuid and message "QUESTION DELETED"
     */
    @RequestMapping(method = RequestMethod.DELETE, path = "/delete/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDeleteResponse> deleteQuestion(@PathVariable("questionId") final String questionId, @RequestHeader("authorization") final String accessToken) throws AuthorizationFailedException, InvalidQuestionException {
        final UserAuthEntity userAuthEntity = commonService.authorizeAccessToken(accessToken);
        final QuestionEntity questionEntity = questionService.getQuestionByUuid(questionId);

        final QuestionEntity deletedQuestion = questionService.deleteQuestionByUserOrAdmin(userAuthEntity, questionEntity);
        final QuestionDeleteResponse questionDeleteResponse = new QuestionDeleteResponse().id(deletedQuestion.getUuid()).status("QUESTION DELETED");

        return new ResponseEntity<QuestionDeleteResponse>(questionDeleteResponse, HttpStatus.OK);
    }

    /**
     * This method is ued to reduce boiler plate code and this returns all the questions created by a user
     *
     * @param allQuestions list of all questions in the entity format
     *
     * returns list of questions in the response i.e json format
     */
    public List<QuestionDetailsResponse> convertQuestionListToJson(final List<QuestionEntity> allQuestions) {
        final List<QuestionDetailsResponse> questionDetailsResponses = new ArrayList<>();

        for (QuestionEntity question : allQuestions) {
            QuestionDetailsResponse questionDetailsResponse = new QuestionDetailsResponse().id(question.getUuid())
                    .content(question.getContent());
            questionDetailsResponses.add(questionDetailsResponse);
        }

        return questionDetailsResponses;
    }
}

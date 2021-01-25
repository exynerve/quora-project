package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnswerService {

    @Autowired
    AnswerDao answerDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity createAnswer(AnswerEntity answerEntity) {
        return answerDao.createAnswer(answerEntity);
    }

    /**
     * This method passes the edited answer to Dao for merging
     * @throws AuthorizationFailedException If non-owner tries to edit an answer
     * returns AnswerEntity
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity editAnswerByOwner(final String editedAnswerContent, final AnswerEntity answerEntity, final UserAuthEntity userAuthEntity) throws AuthorizationFailedException {
        if (userAuthEntity.getUser() != answerEntity.getUserId()) {
            throw new AuthorizationFailedException("ATHR-003", "Only the answer owner can edit the answer");
        }

        answerEntity.setAns(editedAnswerContent);
        answerDao.updateAnswerContent(answerEntity);
        return answerEntity;
    }

    public AnswerEntity getAnswerByUuid(String answerId) throws AnswerNotFoundException {
        AnswerEntity answerEntity = answerDao.getAnswerByUuid(answerId);
        if (answerEntity == null) {
            throw new AnswerNotFoundException("ANS-001","Entered answer uuid does not exist");
        }
        return answerEntity;
    }
}

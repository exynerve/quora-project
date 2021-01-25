package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuestionService {

    @Autowired
    private QuestionDao questionDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(final QuestionEntity questionEntity) {
        return questionDao.createQuestion(questionEntity);
    }

    public List<QuestionEntity> getAllQuestions() {
        return questionDao.getAllQuestions();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity editQuestionContentByOwner(final String editedContent, final QuestionEntity questionEntity, final UserAuthEntity userAuthEntity) throws AuthorizationFailedException {
        if (userAuthEntity.getUser() != questionEntity.getUserId()) {
            throw new AuthorizationFailedException("ATHR-003","Only the question owner can edit the question");
        }

        questionEntity.setContent(editedContent);
        questionDao.updateQuestionContent(questionEntity);
        return questionEntity;
    }

    public QuestionEntity getQuestionByUuid(final String questionId) throws InvalidQuestionException {
        QuestionEntity questionEntity = questionDao.getQuestionByUuid(questionId);
        if (questionEntity == null) {
            throw new InvalidQuestionException("QUES-001","The question with entered uuid whose details are to be seen does not exist");
        }
        return questionEntity;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity deleteQuestionByUserOrAdmin(final UserAuthEntity userAuthEntity, final QuestionEntity questionEntity) throws AuthorizationFailedException {
        if (userAuthEntity.getUser() != questionEntity.getUserId() || (userAuthEntity.getUser() != questionEntity.getUserId() && !userAuthEntity.getUser().getRole().equals("admin"))) {
            throw new AuthorizationFailedException("ATHR-003","Only the question owner or admin can delete the question");
        }

        questionDao.deleteQuestion(questionEntity);
        return questionEntity;
    }

    public List<QuestionEntity> getAllQuestionsByUser(final UserEntity userEntity) {
        return questionDao.getAllQuestionsByUser(userEntity.getId());
    }
}

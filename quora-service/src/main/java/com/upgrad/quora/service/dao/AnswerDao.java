package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class AnswerDao {

    @PersistenceContext
    EntityManager entityManager;

    public AnswerEntity createAnswer(AnswerEntity answerEntity) {
        entityManager.persist(answerEntity);
        return answerEntity;
    }

    public void updateAnswerContent(AnswerEntity answerEntity) {
        entityManager.merge(answerEntity);
    }

    // null value is returned if entity/data is not found
    public AnswerEntity getAnswerByUuid(final String answerId) {
        try {
            return entityManager.createNamedQuery("answerByUuid", AnswerEntity.class).setParameter("uuid", answerId).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

}

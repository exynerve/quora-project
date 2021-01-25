package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    public UserEntity createUser(UserEntity userEntity) {
        entityManager.persist(userEntity);
        return userEntity;
    }

    public UserEntity getUser(final String userUuid) {
        try {
            return entityManager.createNamedQuery("userByUuid", UserEntity.class).setParameter("uuid", userUuid)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public UserAuthEntity createAuthToken(final UserAuthEntity userAuthEntity) {
        entityManager.persist(userAuthEntity);
        return userAuthEntity;
    }


    public UserAuthEntity getUserAuth(final String accessToken) {
        try {
            UserAuthEntity singleResult = entityManager.createNamedQuery("userAuthByAccessToken", UserAuthEntity.class).setParameter("accessToken", accessToken).getSingleResult();
            return singleResult;
        } catch (NoResultException nre) {

            return null;
        }

    }

    public void deleteUser(final UserEntity user){
        entityManager.remove(user);
    }

    public UserEntity getUserByUsername(String username) {
        try {
            return entityManager.createNamedQuery("userByUsername", UserEntity.class).setParameter("username", username)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public UserEntity getUserByEmail(String email) {
        try {
            return entityManager.createNamedQuery("userByEmail", UserEntity.class).setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public void loggedOut(UserAuthEntity userAuthEntity) {
        entityManager.merge(userAuthEntity);
    }
}

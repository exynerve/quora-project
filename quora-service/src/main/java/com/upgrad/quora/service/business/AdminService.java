package com.upgrad.quora.service.business;


import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class AdminService {

    @Autowired
    private UserDao userDao;

    public UserEntity deleteUser(String userId) throws UserNotFoundException {
        UserEntity user = userDao.getUser(userId);
        if(user==null){
            throw new UserNotFoundException("USR-001", "User with entered uuid to be deleted does not exist");
        }

        userDao.deleteUser(user);
        return user;
    }

    public UserAuthEntity authenticate(String authorization) throws AuthorizationFailedException {
        UserAuthEntity userAuthEntity = userDao.getUserAuth(authorization);
        if(userAuthEntity==null){
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        if(userAuthEntity.getLogoutAt()!=null){
            throw new AuthorizationFailedException("ATHR-002", "User is signed out");
        }
        if(userAuthEntity.getUser().getRole().equals("nonadmin")){
            throw new AuthorizationFailedException("ATHR-003","Unauthorized Access, Entered user is not an admin");
        }
        return userAuthEntity;
    }
}

package com.upgrad.quora.service.business;


import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.UUID;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;

    public UserEntity createUser(UserEntity user) throws SignUpRestrictedException {

        String password = user.getPassword();
        if (password == null) {
            user.setPassword("proman@123");
        }
        String[] encryptedText = cryptographyProvider.encrypt(user.getPassword());
        user.setSalt(encryptedText[0]);
        user.setPassword(encryptedText[1]);
        if(userDao.getUserByUsername(user.getUsername())!=null){
            throw new SignUpRestrictedException("SGR-001","Try any other Username, this Username has already been taken");
        }
        if(userDao.getUserByEmail(user.getEmail())!=null){
            throw new SignUpRestrictedException("SGR-002", "This user has already been registered, try with any other emailId");
        }
        return userDao.createUser(user);
    }

    public UserAuthEntity authenticate(String username, String password) throws AuthenticationFailedException {

        UserEntity user = userDao.getUserByUsername(username);
        if(user == null){
            throw new AuthenticationFailedException("ATH-001", "This username does not exist");
        }

        final String encryptedPassword = cryptographyProvider.encrypt(password, user.getSalt());
        if (encryptedPassword.equals(user.getPassword())) {
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
            UserAuthEntity userAuthToken = new UserAuthEntity();
            final LocalDateTime now = LocalDateTime.now();
            final LocalDateTime expiresAt = now.plusHours(8);
            userAuthToken.setUser(user);
            userAuthToken.setAccessToken(jwtTokenProvider.generateToken(user.getUuid(), ZonedDateTime.now(), ZonedDateTime.now().plusHours(8)));
            userAuthToken.setLoginAt(now);
            userAuthToken.setExpiresAt(expiresAt);
            userAuthToken.setUuid(UUID.randomUUID().toString());

            userDao.createAuthToken(userAuthToken);

            return userAuthToken;
        }
        else{
            throw new AuthenticationFailedException("ATH-002", "Password failed");
        }
    }

    public UserAuthEntity logoutUser(String accessToken) throws SignOutRestrictedException {
        UserAuthEntity userAuthEntity = userDao.getUserAuth(accessToken);
        if(userAuthEntity==null) {
            throw new SignOutRestrictedException("SGR-001", "User is not Signed in");
        }
        else{
            userAuthEntity.setLogoutAt(LocalDateTime.now());
            userDao.loggedOut(userAuthEntity);
            return userAuthEntity;
        }
    }
}

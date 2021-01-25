package com.upgrad.quora.api.controller;


import com.upgrad.quora.api.model.SigninResponse;
import com.upgrad.quora.api.model.SignoutResponse;
import com.upgrad.quora.api.model.SignupUserRequest;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.service.business.UserService;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(method = RequestMethod.POST, path = "/signup", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupUserResponse> signupUser(final SignupUserRequest signupUserRequest) throws SignUpRestrictedException {

    final UserEntity user = new UserEntity();
    user.setUuid(UUID.randomUUID().toString());
    user.setAboutme(signupUserRequest.getAboutMe());
    user.setContactnumber(signupUserRequest.getContactNumber());
    user.setCountry(signupUserRequest.getCountry());
    user.setDob(signupUserRequest.getDob());
    user.setEmail(signupUserRequest.getEmailAddress());
    user.setFirstName(signupUserRequest.getFirstName());
    user.setLastName(signupUserRequest.getLastName());
    user.setPassword(signupUserRequest.getPassword());
    user.setUsername(signupUserRequest.getUserName());
    user.setSalt("upgrad@123");
    user.setRole("nonadmin");

    final UserEntity createdEntity = userService.createUser(user);
    final SignupUserResponse userResponse = new SignupUserResponse().id(createdEntity.getUuid()).status("USER SUCCESSFULLY REGISTERED");

        return new ResponseEntity<SignupUserResponse>( userResponse, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/signin", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SigninResponse> signinUser(@RequestHeader("authorization") final String authorization) throws AuthenticationFailedException {

        byte[] decode = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
        String decodedText = new String(decode);
        String[] decodedArray = decodedText.split(":");

        final UserAuthEntity userAuthToken = userService.authenticate(decodedArray[0], decodedArray[1]);
        final UserEntity user = userAuthToken.getUser();
        final SigninResponse signinResponse = new SigninResponse().id(user.getUuid()).message("SIGNED IN SUCCESSFULLY");

        final HttpHeaders headers = new HttpHeaders();
        headers.add("access-token", userAuthToken.getAccessToken());
        return new ResponseEntity<SigninResponse>(signinResponse, headers, HttpStatus.ACCEPTED);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/signout", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignoutResponse> signoutUser(@RequestHeader("authorization") final String accessToken) throws SignOutRestrictedException {
        final UserAuthEntity userAuthEntity = userService.logoutUser(accessToken);
        final UserEntity user = userAuthEntity.getUser();
        final SignoutResponse signoutResponse = new SignoutResponse().id(user.getUuid()).message("SIGNED OUT SUCCESSFULLY");
        return new ResponseEntity<>(signoutResponse, HttpStatus.ACCEPTED);
    }

}


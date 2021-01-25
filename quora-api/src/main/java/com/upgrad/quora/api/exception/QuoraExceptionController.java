package com.upgrad.quora.api.exception;

import com.upgrad.quora.api.model.ErrorResponse;
import com.upgrad.quora.service.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class QuoraExceptionController {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> userNotFound(UserNotFoundException exception, WebRequest request){

        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exception.getCode()).message(exception.getErrorMessage()), HttpStatus.NOT_FOUND );
    }

    @ExceptionHandler(SignUpRestrictedException.class)
    public ResponseEntity<ErrorResponse> signUpRestricted(SignUpRestrictedException exception, WebRequest request){

        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exception.getCode()).message(exception.getErrorMessage()), HttpStatus.CONFLICT );
    }

    @ExceptionHandler(SignOutRestrictedException.class)
    public ResponseEntity<ErrorResponse> signOutRestricted(SignOutRestrictedException exception, WebRequest request){

        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exception.getCode()).message(exception.getErrorMessage()), HttpStatus.UNAUTHORIZED );
    }

    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<ErrorResponse> authenticationFailed(AuthorizationFailedException exception, WebRequest request){

        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exception.getCode()).message(exception.getErrorMessage()), HttpStatus.UNAUTHORIZED );
    }

    @ExceptionHandler(AuthorizationFailedException.class)
    public ResponseEntity<ErrorResponse> authorizationFailed(AuthorizationFailedException exception, WebRequest request){

        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exception.getCode()).message(exception.getErrorMessage()), HttpStatus.FORBIDDEN );
    }

    @ExceptionHandler(InvalidQuestionException.class)
    public ResponseEntity<ErrorResponse> invalidQuestionException(InvalidQuestionException exe, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exe.getCode()).message(exe.getErrorMessage()).
                        rootCause("Question UUID is wrong or does not exists"), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AnswerNotFoundException.class)
    public ResponseEntity<ErrorResponse> answerNotFoundException(AnswerNotFoundException exe, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exe.getCode()).message(exe.getErrorMessage()).
                        rootCause("Answer not found"), HttpStatus.NOT_FOUND);
    }
}

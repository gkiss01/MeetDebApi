package com.gkiss01.meetdebwebapi.exception;

import com.gkiss01.meetdebwebapi.model.GenericResponse;
import com.gkiss01.meetdebwebapi.utils.CustomFileNotFoundException;
import com.gkiss01.meetdebwebapi.utils.CustomRuntimeException;
import com.gkiss01.meetdebwebapi.utils.ErrorCodes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<String> errors = new ArrayList<>();
        for (ObjectError objectError : exception.getBindingResult().getAllErrors()) {
            errors.add(objectError.getDefaultMessage());
        }
        GenericResponse response = GenericResponse.builder().error(true).errorCode(ErrorCodes.BAD_USER_REQUEST_FORMAT.getCode()).errors(errors).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ExceptionHandler(value = {MaxUploadSizeExceededException.class})
    public ResponseEntity<Object> handleMaxUploadSizeExceeded() {
        GenericResponse response = GenericResponse.builder().error(true).errors(Collections.singletonList("Size limit is exceeded!")).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ExceptionHandler(value = {CustomFileNotFoundException.class})
    public ResponseEntity<Object> handleCustomFileNotFound() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(value = {CustomRuntimeException.class})
    public ResponseEntity<Object> handleCustomRuntimeException(CustomRuntimeException exception) {
        String error;
        switch (exception.getErrorCode()) {
            case USER_NOT_FOUND: error = "User is not found!"; break;
            case NO_USERS_FOUND: error = "No users found!"; break;
            case CONFIRMATION_TOKEN_NOT_FOUND: error = "Confirmation token is not found!"; break;
            case EMAIL_ALREADY_IN_USE: error = "Email is already in use!"; break;
            case USER_ALREADY_VERIFIED: error = "User is already verified!"; break;
            case BAD_USER_REQUEST_FORMAT: error = "Bad user request format!"; break;

            case EVENT_NOT_FOUND: error = "Event is not found!"; break;
            case NO_EVENTS_FOUND: error = "No events found!"; break;
            case BAD_EVENT_REQUEST_FORMAT: error = "Bad event request format!"; break;

            case PARTICIPANT_NOT_FOUND: error = "Participant is not found!"; break;
            case NO_PARTICIPANTS_FOUND: error = "No participants found!"; break;
            case PARTICIPANT_ALREADY_CREATED: error = "Participant is already created!"; break;

            case ACCESS_DENIED: error = "Access is denied!"; break;

            default: error = "Unknown error!";
        }

        GenericResponse response = GenericResponse.builder().error(true).errorCode(exception.getErrorCode().getCode()).errors(Collections.singletonList(error)).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<Object> handleOtherExceptions(Exception exception) throws Exception {
        if (exception instanceof AccessDeniedException || exception instanceof AuthenticationException) {
            throw exception;
        }

        GenericResponse response = GenericResponse.builder().error(true).errors(Collections.singletonList(exception.getLocalizedMessage())).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

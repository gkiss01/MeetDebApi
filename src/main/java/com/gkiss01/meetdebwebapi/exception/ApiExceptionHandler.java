package com.gkiss01.meetdebwebapi.exception;

import com.gkiss01.meetdebwebapi.model.GenericResponse;
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

import java.util.Collections;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception, HttpHeaders headers, HttpStatus status, WebRequest request) {
        GenericResponse response = GenericResponse.builder().error(true).build();
        for (ObjectError objectError : exception.getBindingResult().getAllErrors()) {
            response.addError(objectError.getDefaultMessage());
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ExceptionHandler(value = {MaxUploadSizeExceededException.class})
    public ResponseEntity<Object> handleMaxUploadSizeExceeded() {
        GenericResponse response = GenericResponse.builder().error(true).errors(Collections.singletonList("Size limit is exceeded!")).build();
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

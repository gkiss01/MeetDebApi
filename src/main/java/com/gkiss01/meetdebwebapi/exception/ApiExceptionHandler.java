package com.gkiss01.meetdebwebapi.exception;

import com.gkiss01.meetdebwebapi.model.ErrorResponse;
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
import java.util.List;

import static com.gkiss01.meetdebwebapi.utils.Utils.getHttpStatusFromErrorCode;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<String> errors = new ArrayList<>();
        for (ObjectError objectError : exception.getBindingResult().getAllErrors()) {
            errors.add(objectError.getDefaultMessage());
        }
        ErrorResponse response = ErrorResponse.builder().errorCode(ErrorCodes.BAD_REQUEST_FORMAT).errors(errors).build();
        return new ResponseEntity<>(response, getHttpStatusFromErrorCode(ErrorCodes.BAD_REQUEST_FORMAT));
    }

    @ExceptionHandler(value = {MaxUploadSizeExceededException.class})
    public ResponseEntity<Object> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException exception) {
        ErrorResponse response = ErrorResponse.builder().errorCode(ErrorCodes.FILE_SIZE_LIMIT_EXCEEDED)
                .errors(List.of("Max file size is " + exception.getMaxUploadSize() / 1000 + " kB!")).build();
        return new ResponseEntity<>(response, getHttpStatusFromErrorCode(ErrorCodes.FILE_SIZE_LIMIT_EXCEEDED));
    }

    @ExceptionHandler(value = {CustomFileNotFoundException.class})
    public ResponseEntity<Object> handleCustomFileNotFound() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(value = {CustomRuntimeException.class})
    public ResponseEntity<Object> handleCustomRuntimeException(CustomRuntimeException exception) {
        ErrorResponse response = ErrorResponse.builder().errorCode(exception.getErrorCode()).build();
        return new ResponseEntity<>(response, getHttpStatusFromErrorCode(exception.getErrorCode()));
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<Object> handleOtherExceptions(Exception exception) throws Exception {
        if (exception instanceof AccessDeniedException || exception instanceof AuthenticationException) {
            throw exception;
        }

        ErrorResponse response = ErrorResponse.builder().errorCode(ErrorCodes.UNKNOWN)
                .errors(List.of(exception.getLocalizedMessage())).build();
        return new ResponseEntity<>(response, getHttpStatusFromErrorCode(ErrorCodes.UNKNOWN));
    }
}

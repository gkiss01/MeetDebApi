package com.gkiss01.meetdebwebapi.utils;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CustomFileNotFoundException extends RuntimeException {
    public CustomFileNotFoundException(String message) {
        super(message);
    }
}
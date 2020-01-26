package com.gkiss01.meetdebwebapi.utils;

import lombok.Getter;

@Getter
public enum ErrorCodes {
    USER_NOT_FOUND(100),
    NO_USERS_FOUND(101),
    CONFIRMATION_TOKEN_NOT_FOUND(102),
    USER_ALREADY_VERIFIED(103),
    EMAIL_ALREADY_IN_USE(104),
    BAD_USER_REQUEST_FORMAT(105);

    private final int code;

    ErrorCodes(int code) {
        this.code = code;
    }
}

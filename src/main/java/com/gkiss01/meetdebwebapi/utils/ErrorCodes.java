package com.gkiss01.meetdebwebapi.utils;

import lombok.Getter;

@Getter
public enum ErrorCodes {
    ACCESS_DENIED(10),

    USER_NOT_FOUND(100),
    NO_USERS_FOUND(101),
    CONFIRMATION_TOKEN_NOT_FOUND(102),
    USER_ALREADY_VERIFIED(103),
    EMAIL_ALREADY_IN_USE(104),
    BAD_USER_REQUEST_FORMAT(105),

    EVENT_NOT_FOUND(200),
    NO_EVENTS_FOUND(201),
    BAD_EVENT_REQUEST_FORMAT(205),

    PARTICIPANT_NOT_FOUND(300),
    NO_PARTICIPANTS_FOUND(301),
    PARTICIPANT_ALREADY_CREATED(304);

    private final int code;

    ErrorCodes(int code) {
        this.code = code;
    }
}

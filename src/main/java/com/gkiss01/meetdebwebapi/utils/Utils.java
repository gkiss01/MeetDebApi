package com.gkiss01.meetdebwebapi.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.gkiss01.meetdebwebapi.model.ErrorResponse;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

public class Utils {
    public static void errorResponse(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, ErrorCodes errorCode) throws IOException {
        ErrorResponse response = ErrorResponse.builder().errorCode(errorCode).build();
        OutputStream out = httpServletResponse.getOutputStream();

        ObjectMapper mapper;
        if (httpServletRequest.getHeader("Accept") != null && httpServletRequest.getHeader("Accept").equals("application/xml")) {
            httpServletResponse.setContentType("application/xml");
            mapper = new XmlMapper();
        }
        else {
            httpServletResponse.setContentType("application/json");
            mapper = new ObjectMapper();
        }

        httpServletResponse.setStatus(getHttpStatusFromErrorCode(errorCode).value());
        mapper.writeValue(out, response);
        out.flush();
    }

    public static HttpStatus getHttpStatusFromErrorCode(ErrorCodes errorCode) {
        return switch (errorCode) {
            case USER_NOT_FOUND, CONFIRMATION_TOKEN_NOT_FOUND, EVENT_NOT_FOUND, PARTICIPANT_NOT_FOUND, DATE_NOT_FOUND,
                    VOTE_NOT_FOUND, FILE_NOT_FOUND, NO_USERS_FOUND, NO_EVENTS_FOUND, NO_DATES_FOUND -> HttpStatus.NOT_FOUND;

            case EMAIL_ALREADY_IN_USE, USER_ALREADY_VERIFIED, PARTICIPANT_ALREADY_CREATED,
                    DATE_ALREADY_CREATED, VOTE_ALREADY_CREATED -> HttpStatus.CONFLICT;

            case FILENAME_INVALID, COULD_NOT_CONVERT_IMAGE, FILE_SIZE_LIMIT_EXCEEDED -> HttpStatus.UNPROCESSABLE_ENTITY;

            case UPLOAD_FAILED, COULD_NOT_CREATE_DIRECTORY -> HttpStatus.INTERNAL_SERVER_ERROR;

            case BAD_REQUEST_FORMAT -> HttpStatus.BAD_REQUEST;

            case ACCESS_DENIED -> HttpStatus.FORBIDDEN;

            case USER_DISABLED_OR_NOT_VALID -> HttpStatus.UNAUTHORIZED;

            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}

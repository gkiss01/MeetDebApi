package com.gkiss01.meetdebwebapi.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.gkiss01.meetdebwebapi.model.GenericResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;

public class Utils {
    public static void errorResponse(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, ErrorCodes errorCode) throws IOException {
        GenericResponse response = GenericResponse.builder().error(true).errorCode(errorCode)
                .errors(Collections.singletonList(getErrorString(errorCode))).build();
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

        httpServletResponse.setStatus(HttpServletResponse.SC_OK);
        mapper.writeValue(out, response);
        out.flush();
    }

    public static String getErrorString(ErrorCodes errorCode) {
        switch (errorCode) {
            case USER_NOT_FOUND: return "User is not found!";
            case NO_USERS_FOUND: return "No users found!";
            case CONFIRMATION_TOKEN_NOT_FOUND: return "Confirmation token is not found!";
            case EMAIL_ALREADY_IN_USE: return "Email is already in use!";
            case USER_ALREADY_VERIFIED: return "User is already verified!";

            case EVENT_NOT_FOUND: return "Event is not found!";
            case NO_EVENTS_FOUND: return "No events found!";

            case PARTICIPANT_NOT_FOUND: return "Participant is not found!";
            case NO_PARTICIPANTS_FOUND: return "No participants found!";
            case PARTICIPANT_ALREADY_CREATED: return "Participant is already created!";

            case DATE_NOT_FOUND: return "Date is not found!";
            case NO_DATES_FOUND: return "No dates found!";
            case DATE_ALREADY_CREATED: return "Date is already created!";

            case VOTE_NOT_FOUND: return "Vote is not found!";
            case VOTE_ALREADY_CREATED: return "Vote is already created!";

            case FILE_NOT_FOUND: return "File is not found!";
            case FILENAME_INVALID: return "Filename is invalid!";
            case COULD_NOT_CONVERT_IMAGE: return "Could not convert image!";
            case FILE_SIZE_LIMIT_EXCEEDED: return "Size limit is exceeded!";
            case UPLOAD_FAILED: return "Upload failed!";
            case COULD_NOT_CREATE_DIRECTORY: return "Could not create directory!";

            case BAD_REQUEST_FORMAT: return "Bad request format!";
            case ACCESS_DENIED: return "Access is denied!";
            case USER_DISABLED_OR_NOT_VALID: return "User is disabled or not valid!";

            default: return "Unknown error!";
        }
    }
}

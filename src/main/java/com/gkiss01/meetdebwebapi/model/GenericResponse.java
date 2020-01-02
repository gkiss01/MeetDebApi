package com.gkiss01.meetdebwebapi.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class GenericResponse {

    private Boolean error;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> errors = new ArrayList<>();

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String message;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private UserResponse user;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<UserResponse> users;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private EventResponse event;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<EventResponse> events;

    public void addError(String error) {
        errors.add(error);
    }
}
package com.gkiss01.meetdebwebapi.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class GenericResponse {

    private Boolean error;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String message;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> errors = new ArrayList<>();

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private UserResponse user;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<UserResponse> users;

    public GenericResponse(Boolean error, UserResponse user) {
        this.error = error;
        this.user = user;
        this.users = null;
        this.message = null;
    }
    public GenericResponse(Boolean error, UserResponse user, List<UserResponse> users) {
        this(error, user);
        this.users = users;
    }
    public GenericResponse(Boolean error, UserResponse user, List<UserResponse> users, String message) {
        this(error, user, users);
        this.message = message;
    }

    public void addError(String error) {
        errors.add(error);
    }
}
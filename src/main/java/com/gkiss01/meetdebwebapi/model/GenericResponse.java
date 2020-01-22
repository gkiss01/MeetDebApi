package com.gkiss01.meetdebwebapi.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gkiss01.meetdebwebapi.entity.Date;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GenericResponse {

    private Boolean error;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> errors;

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

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private ParticipantResponse participant;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<ParticipantResponse> participants;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Date date;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Date> dates;

    public void addError(String error) {
        errors.add(error);
    }
}
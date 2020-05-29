package com.gkiss01.meetdebwebapi.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gkiss01.meetdebwebapi.utils.ErrorCodes;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.List;

@Getter
@Builder
public class ErrorResponse {

    private final Boolean error;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Enumerated(EnumType.STRING)
    private final ErrorCodes errorCode;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final List<String> errors;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final Long withId;
}
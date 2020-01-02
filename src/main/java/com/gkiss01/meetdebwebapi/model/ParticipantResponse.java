package com.gkiss01.meetdebwebapi.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantResponse {
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Long eventId;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Long userId;

    private String name;
}

package com.gkiss01.meetdebwebapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventResponse {
    private Long id;

    private String username;

    private OffsetDateTime date;

    private String venue;

    private String labels;
}

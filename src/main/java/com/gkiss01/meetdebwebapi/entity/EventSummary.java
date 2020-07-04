package com.gkiss01.meetdebwebapi.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventSummary {
    private Long userId;
    private Long eventsCreated;
    private Long eventsInvolved;
}

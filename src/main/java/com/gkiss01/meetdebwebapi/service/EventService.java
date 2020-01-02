package com.gkiss01.meetdebwebapi.service;

import com.gkiss01.meetdebwebapi.entity.Event;
import com.gkiss01.meetdebwebapi.model.EventRequest;
import com.gkiss01.meetdebwebapi.utils.UserWithId;

import java.util.List;

public interface EventService {
    Event createEvent(EventRequest eventRequest, Long userId);
    Event updateEvent(Long eventId, EventRequest eventRequest, UserWithId userDetails);
    void deleteEvent(Long eventId, UserWithId userDetails);
    List<Event> getEvents(int page, int limit);
}

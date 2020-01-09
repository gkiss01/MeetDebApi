package com.gkiss01.meetdebwebapi.service;

import com.gkiss01.meetdebwebapi.entity.Event;
import com.gkiss01.meetdebwebapi.entity.Participant;
import com.gkiss01.meetdebwebapi.utils.UserWithId;

import java.util.List;

public interface ParticipantService {
    Event createParticipant(Long eventId, UserWithId userDetails);
    Event deleteParticipant(Long eventId, UserWithId userDetails);
    Event deleteParticipant(Long eventId, Long userId);
    List<Participant> getParticipants(Long eventId, int page, int limit);
}

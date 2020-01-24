package com.gkiss01.meetdebwebapi.service.impl;

import com.gkiss01.meetdebwebapi.entity.Event;
import com.gkiss01.meetdebwebapi.entity.Participant;
import com.gkiss01.meetdebwebapi.entity.idclass.ParticipantId;
import com.gkiss01.meetdebwebapi.repository.EventRepository;
import com.gkiss01.meetdebwebapi.repository.ParticipantRepository;
import com.gkiss01.meetdebwebapi.repository.VoteRepository;
import com.gkiss01.meetdebwebapi.service.ParticipantService;
import com.gkiss01.meetdebwebapi.utils.UserWithId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ParticipantServiceImpl implements ParticipantService {

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Override
    @Transactional
    public Event createParticipant(Long eventId, UserWithId userDetails) {
        Event event = eventRepository.findEventByIdCustom(eventId, userDetails.getUserId());

        if (event == null)
            throw new RuntimeException("Event not found!");

        if (event.getAccepted())
            throw new RuntimeException("Participant is already created!");

        Participant participant = new Participant(new ParticipantId(eventId, userDetails.getUserId()), null);
        voteRepository.deleteByEventIdAndUserId(eventId, userDetails.getUserId());
        participantRepository.save(participant);
        return eventRepository.findEventByIdCustom(eventId, userDetails.getUserId());
    }

    @Override
    @Transactional
    public Event deleteParticipant(Long eventId, UserWithId userDetails) {
        return deleteParticipant(eventId, userDetails.getUserId());
    }

    @Override
    @Transactional
    public Event deleteParticipant(Long eventId, Long userId) {
        Participant participant = participantRepository.findParticipantById_EventIdAndId_UserId(eventId, userId);

        if (participant == null)
            throw new RuntimeException("Participant not found!");

        participantRepository.delete(participant);
        return eventRepository.findEventByIdCustom(eventId, userId);
    }

    @Override
    public List<Participant> getParticipants(Long eventId, int page, int limit) {
        if (!eventRepository.existsEventById(eventId))
            throw new RuntimeException("Event not found!");

        Pageable pageableRequest = PageRequest.of(page, limit);
        List<Participant> participantEntities = participantRepository.findParticipantById_EventIdCustom(eventId, pageableRequest);

        if (participantEntities.isEmpty())
            throw new RuntimeException("No participants found!");

        return participantEntities;
    }
}
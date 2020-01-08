package com.gkiss01.meetdebwebapi.service.impl;

import com.gkiss01.meetdebwebapi.entity.Event;
import com.gkiss01.meetdebwebapi.entity.Participant;
import com.gkiss01.meetdebwebapi.entity.User;
import com.gkiss01.meetdebwebapi.entity.idclass.ParticipantId;
import com.gkiss01.meetdebwebapi.repository.EventRepository;
import com.gkiss01.meetdebwebapi.repository.ParticipantRepository;
import com.gkiss01.meetdebwebapi.repository.UserRepository;
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
    private UserRepository userRepository;

    @Override
    public Participant createParticipant(Long eventId, UserWithId userDetails) {
        if (participantRepository.findParticipantById_Event_IdAndId_User_Id(eventId, userDetails.getUserId()) != null)
            throw new RuntimeException("Participant is already created!");

        Event event = eventRepository.findEventById(eventId);

        if (event == null)
            throw new RuntimeException("Event not found!");

        User user = userRepository.findUserById(userDetails.getUserId());

        if (user == null)
            throw new RuntimeException("User not found!");

        Participant participant = new Participant(new ParticipantId(event, user));

        participant = participantRepository.save(participant);
        return participant;
    }

    @Override
    @Transactional
    public void deleteParticipant(Long eventId, UserWithId userDetails) {
        deleteParticipant(eventId, userDetails.getUserId());
    }

    @Override
    @Transactional
    public void deleteParticipant(Long eventId, Long userId) {
        Participant participant = participantRepository.findParticipantById_Event_IdAndId_User_Id(eventId, userId);

        if (participant == null)
            throw new RuntimeException("Participant not found!");

        participantRepository.delete(participant);
    }

    @Override
    public List<Participant> getParticipants(Long eventId, int page, int limit) {
        if (!eventRepository.existsEventById(eventId))
            throw new RuntimeException("Event not found!");

        Pageable pageableRequest = PageRequest.of(page, limit);
        List<Participant> participantEntities = participantRepository.findById_Event_Id(eventId, pageableRequest);

        if (participantEntities.isEmpty())
            throw new RuntimeException("No participants found!");

        return participantEntities;
    }
}
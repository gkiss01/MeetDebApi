package com.gkiss01.meetdebwebapi.service.impl;

import com.gkiss01.meetdebwebapi.entity.Event;
import com.gkiss01.meetdebwebapi.entity.Participant;
import com.gkiss01.meetdebwebapi.entity.idclass.ParticipantId;
import com.gkiss01.meetdebwebapi.repository.EventRepository;
import com.gkiss01.meetdebwebapi.repository.ParticipantRepository;
import com.gkiss01.meetdebwebapi.repository.VoteRepository;
import com.gkiss01.meetdebwebapi.service.ParticipantService;
import com.gkiss01.meetdebwebapi.utils.CustomRuntimeException;
import com.gkiss01.meetdebwebapi.utils.ErrorCodes;
import com.gkiss01.meetdebwebapi.utils.UserWithId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ParticipantServiceImpl implements ParticipantService {

    private final ParticipantRepository participantRepository;
    private final EventRepository eventRepository;
    private final VoteRepository voteRepository;

    @Autowired
    public ParticipantServiceImpl(ParticipantRepository participantRepository, EventRepository eventRepository, VoteRepository voteRepository) {
        this.participantRepository = participantRepository;
        this.eventRepository = eventRepository;
        this.voteRepository = voteRepository;
    }

    @Override
    @Transactional
    public Event modifyParticipation(Long eventId, UserWithId userDetails) {
        Event event = eventRepository.findEventByIdCustom(eventId, userDetails.getUserId());

        if (event == null)
            throw new CustomRuntimeException(ErrorCodes.EVENT_NOT_FOUND);

        if (event.getAccepted()) {
            Participant participant = participantRepository.findParticipantById_EventIdAndId_UserId(eventId, userDetails.getUserId());
            participantRepository.delete(participant);
        } else {
            Participant participant = new Participant(new ParticipantId(eventId, userDetails.getUserId()), null);
            voteRepository.deleteByEventIdAndUserId(eventId, userDetails.getUserId());
            participantRepository.save(participant);
        }

        return eventRepository.findEventByIdCustom(eventId, userDetails.getUserId());
    }

    @Override
    @Transactional
    public void deleteParticipant(Long eventId, Long userId) {
        Participant participant = participantRepository.findParticipantById_EventIdAndId_UserId(eventId, userId);

        if (participant == null)
            throw new CustomRuntimeException(ErrorCodes.PARTICIPANT_NOT_FOUND);

        participantRepository.delete(participant);
    }

    @Override
    public List<Participant> getParticipants(Long eventId, int page, int limit) {
        if (!eventRepository.existsEventById(eventId))
            throw new CustomRuntimeException(ErrorCodes.EVENT_NOT_FOUND);

        Pageable pageableRequest = PageRequest.of(page, limit);
        return participantRepository.findParticipantById_EventIdCustom(eventId, pageableRequest);
    }
}
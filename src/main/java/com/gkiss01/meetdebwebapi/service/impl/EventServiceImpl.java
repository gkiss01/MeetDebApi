package com.gkiss01.meetdebwebapi.service.impl;

import com.gkiss01.meetdebwebapi.entity.Event;
import com.gkiss01.meetdebwebapi.model.EventRequest;
import com.gkiss01.meetdebwebapi.repository.*;
import com.gkiss01.meetdebwebapi.service.EventService;
import com.gkiss01.meetdebwebapi.utils.CustomRuntimeException;
import com.gkiss01.meetdebwebapi.utils.ErrorCodes;
import com.gkiss01.meetdebwebapi.utils.UserWithId;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.gkiss01.meetdebwebapi.entity.Role.ROLE_ADMIN;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final ParticipantRepository participantRepository;
    private final DateRepository dateRepository;
    private final UserRepository userRepository;
    private final VoteRepository voteRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public EventServiceImpl(EventRepository eventRepository, ParticipantRepository participantRepository, DateRepository dateRepository, UserRepository userRepository, VoteRepository voteRepository, ModelMapper modelMapper) {
        this.eventRepository = eventRepository;
        this.participantRepository = participantRepository;
        this.dateRepository = dateRepository;
        this.userRepository = userRepository;
        this.voteRepository = voteRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Event createEvent(EventRequest eventRequest, UserWithId userDetails) {
        Event event = modelMapper.map(eventRequest, Event.class);
        event.setUserId(userDetails.getUserId());

        event = eventRepository.save(event);
        event.setUsername(userRepository.findNameById(event.getUserId()));
        return event;
    }

    @Override
    public Event updateEvent(Long eventId, EventRequest eventRequest, UserWithId userDetails) {
        Event event = eventRepository.findEventByIdCustom(eventId, userDetails.getUserId());

        if (event == null)
            throw new CustomRuntimeException(ErrorCodes.EVENT_NOT_FOUND);

        if (!userDetails.getAuthorities().contains(ROLE_ADMIN) && !userDetails.getUserId().equals(event.getUserId()))
            throw new CustomRuntimeException(ErrorCodes.ACCESS_DENIED);

        if (eventRequest.getName() != null) event.setName(eventRequest.getName());
        if (eventRequest.getDate() != null) event.setDate(eventRequest.getDate());
        if (eventRequest.getVenue() != null) event.setVenue(eventRequest.getVenue());
        if (eventRequest.getDescription() != null) event.setDescription(eventRequest.getDescription());

        eventRepository.save(event);
        return event;
    }

    @Override
    @Transactional
    public void deleteEvent(Long eventId, UserWithId userDetails) {
        Event event = eventRepository.findEventById(eventId);

        if (event == null)
            throw new CustomRuntimeException(ErrorCodes.EVENT_NOT_FOUND);

        if (!userDetails.getAuthorities().contains(ROLE_ADMIN) && !userDetails.getUserId().equals(event.getUserId()))
            throw new CustomRuntimeException(ErrorCodes.ACCESS_DENIED);

        participantRepository.deleteById_EventId(eventId);
        voteRepository.deleteVotesByDateEvent(eventId);
        dateRepository.deleteByEventId(eventId);
        eventRepository.delete(event);
    }

    @Override
    public Event getEvent(Long eventId, UserWithId userDetails) {
        Event event = eventRepository.findEventByIdCustom(eventId, userDetails.getUserId());

        if (event == null)
            throw new CustomRuntimeException(ErrorCodes.EVENT_NOT_FOUND);

        return event;
    }

    @Override
    public List<Event> getEvents(int page, int limit, UserWithId userDetails) {
        Pageable pageableRequest = PageRequest.of(page, limit);

        return eventRepository.findAllByOrderByDateCustom(userDetails.getUserId(), pageableRequest);
    }

    @Override
    public void createReport(Long eventId) {
        Event event = eventRepository.findEventById(eventId);

        if (event == null)
            throw new CustomRuntimeException(ErrorCodes.EVENT_NOT_FOUND);

        if (!event.getReported()) {
            event.setReported(true);

            eventRepository.save(event);
        }
    }

    @Override
    public void removeReport(Long eventId) {
        Event event = eventRepository.findEventById(eventId);

        if (event == null)
            throw new CustomRuntimeException(ErrorCodes.EVENT_NOT_FOUND);

        if (event.getReported()) {
            event.setReported(false);

            eventRepository.save(event);
        }
    }
}

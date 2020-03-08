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

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private DateRepository dateRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private ModelMapper modelMapper;

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
        Event event = eventRepository.findEventById(eventId);

        if (event == null)
            throw new CustomRuntimeException(ErrorCodes.EVENT_NOT_FOUND);

        if (!userDetails.getAuthorities().contains(ROLE_ADMIN) && !userDetails.getUserId().equals(event.getUserId()))
            throw new CustomRuntimeException(ErrorCodes.ACCESS_DENIED);

        event.setDate(eventRequest.getDate());
        event.setVenue(eventRequest.getVenue());
        event.setDescription(eventRequest.getDescription());

        eventRepository.save(event);
        return eventRepository.findEventByIdCustom(eventId, userDetails.getUserId());
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
        List<Event> eventEntities = eventRepository.findAllByOrderByDateCustom(userDetails.getUserId(), pageableRequest);

        if (eventEntities.isEmpty())
            throw new CustomRuntimeException(ErrorCodes.NO_EVENTS_FOUND);

        return eventEntities;
    }
}

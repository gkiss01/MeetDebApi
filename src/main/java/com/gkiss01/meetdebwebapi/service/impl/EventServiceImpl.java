package com.gkiss01.meetdebwebapi.service.impl;

import com.gkiss01.meetdebwebapi.entity.Event;
import com.gkiss01.meetdebwebapi.entity.User;
import com.gkiss01.meetdebwebapi.model.EventRequest;
import com.gkiss01.meetdebwebapi.repository.EventRepository;
import com.gkiss01.meetdebwebapi.repository.ParticipantRepository;
import com.gkiss01.meetdebwebapi.repository.UserRepository;
import com.gkiss01.meetdebwebapi.service.EventService;
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
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public Event createEvent(EventRequest eventRequest, UserWithId userDetails) {
        User user = userRepository.findUserById(userDetails.getUserId());

        if (user == null)
            throw new RuntimeException("User not found!");

        Event event = modelMapper.map(eventRequest, Event.class);
        event.setUser(user);

        event = eventRepository.save(event);
        return event;
    }

    @Override
    public Event updateEvent(Long eventId, EventRequest eventRequest, UserWithId userDetails) {
        Event event = eventRepository.findEventById(eventId);

        if (event == null)
            throw new RuntimeException("Event not found!");

        if (!userDetails.getAuthorities().contains(ROLE_ADMIN) && !userDetails.getUserId().equals(event.getUser().getId()))
            throw new RuntimeException("Access is denied!");

        event.setDate(eventRequest.getDate());
        event.setVenue(eventRequest.getVenue());
        event.setLabels(eventRequest.getLabels());

        event = eventRepository.save(event);
        event.setParticipants(participantRepository.countById_Event_Id(event.getId()));
        event.setAccepted(participantRepository.existsById_Event_IdAndId_User_Id(event.getId(), userDetails.getUserId()));
        return event;
    }

    @Override
    @Transactional
    public void deleteEvent(Long eventId, UserWithId userDetails) {
        Event event = eventRepository.findEventById(eventId);

        if (event == null)
            throw new RuntimeException("Event not found!");

        if (!userDetails.getAuthorities().contains(ROLE_ADMIN) && !userDetails.getUserId().equals(event.getUser().getId()))
            throw new RuntimeException("Access is denied!");

        eventRepository.delete(event);
    }

    @Override
    public List<Event> getEvents(int page, int limit, UserWithId userDetails) {
        Long userId = userDetails.getUserId();

        Pageable pageableRequest = PageRequest.of(page, limit);
        List<Event> eventEntities = eventRepository.findAllByOrderByDate(pageableRequest);
        eventEntities.forEach(e -> e.setParticipants(participantRepository.countById_Event_Id(e.getId())));
        eventEntities.forEach(e -> e.setAccepted(participantRepository.existsById_Event_IdAndId_User_Id(e.getId(), userId)));

        if (eventEntities.isEmpty())
            throw new RuntimeException("No events found!");

        return eventEntities;
    }
}

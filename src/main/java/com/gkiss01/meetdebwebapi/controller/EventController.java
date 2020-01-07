package com.gkiss01.meetdebwebapi.controller;

import com.gkiss01.meetdebwebapi.entity.Event;
import com.gkiss01.meetdebwebapi.model.EventRequest;
import com.gkiss01.meetdebwebapi.model.EventResponse;
import com.gkiss01.meetdebwebapi.model.GenericResponse;
import com.gkiss01.meetdebwebapi.repository.UserRepository;
import com.gkiss01.meetdebwebapi.service.EventService;
import com.gkiss01.meetdebwebapi.utils.UserWithId;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("events")
public class EventController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventService eventService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE },
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public GenericResponse createEvent(@Valid @RequestBody EventRequest eventRequest, Authentication authentication) {
        UserWithId userDetails = (UserWithId) authentication.getPrincipal();

        Event event = eventService.createEvent(eventRequest, userDetails);
        EventResponse response = modelMapper.map(event, EventResponse.class);
        response.setUsername(userRepository.findNameById(event.getUserId()));
        return GenericResponse.builder().error(false).event(response).build();
    }

    @PreAuthorize("hasRole('CLIENT')")
    @PutMapping(path = "/{eventId}", consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE },
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public GenericResponse updateEvent(@PathVariable Long eventId, @Valid @RequestBody EventRequest eventRequest, Authentication authentication) {
        UserWithId userDetails = (UserWithId) authentication.getPrincipal();

        Event event = eventService.updateEvent(eventId, eventRequest, userDetails);
        EventResponse response = modelMapper.map(event, EventResponse.class);
        response.setUsername(userRepository.findNameById(event.getUserId()));
        return GenericResponse.builder().error(false).event(response).build();
    }

    @PreAuthorize("hasRole('CLIENT')")
    @DeleteMapping(path = "/{eventId}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public GenericResponse deleteEvent(@PathVariable Long eventId, Authentication authentication) {
        UserWithId userDetails = (UserWithId) authentication.getPrincipal();

        eventService.deleteEvent(eventId, userDetails);
        return GenericResponse.builder().error(false).message("Event deleted!").build();
    }

    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping(produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public GenericResponse getEvents(@RequestParam(value = "page", defaultValue = "0") int page,
                                     @RequestParam(value = "limit", defaultValue = "25") int limit,
                                     Authentication authentication) {
        UserWithId userDetails = (UserWithId) authentication.getPrincipal();

        if (page > 0) page--;
        List<Event> eventEntities = eventService.getEvents(page, limit, userDetails);
        List<EventResponse> eventResponses = new ArrayList<>();

        eventEntities.forEach(e -> {
            EventResponse eventResponse = modelMapper.map(e, EventResponse.class);
            eventResponse.setUsername(userRepository.findNameById(e.getUserId()));
            eventResponses.add(eventResponse);
        });
        return GenericResponse.builder().error(false).events(eventResponses).build();
    }
}

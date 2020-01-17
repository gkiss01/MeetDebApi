package com.gkiss01.meetdebwebapi.controller;

import com.gkiss01.meetdebwebapi.entity.Event;
import com.gkiss01.meetdebwebapi.model.EventRequest;
import com.gkiss01.meetdebwebapi.model.EventResponse;
import com.gkiss01.meetdebwebapi.model.GenericResponse;
import com.gkiss01.meetdebwebapi.service.EventService;
import com.gkiss01.meetdebwebapi.service.FileService;
import com.gkiss01.meetdebwebapi.utils.UserWithId;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("events")
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private FileService fileService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE },
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public GenericResponse createEvent(@RequestPart("event") @Valid EventRequest eventRequest,
                                       @RequestPart(value = "file", required = false) MultipartFile file,
                                       Authentication authentication) {
        UserWithId userDetails = (UserWithId) authentication.getPrincipal();

        Event event = eventService.createEvent(eventRequest, userDetails);
        if (file != null && !file.isEmpty())
            fileService.storeFile(event.getId(), file);

        return GenericResponse.builder().error(false).event(modelMapper.map(event, EventResponse.class)).build();
    }

    @PreAuthorize("hasRole('CLIENT')")
    @PutMapping(path = "/{eventId}", consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE },
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public GenericResponse updateEvent(@PathVariable Long eventId, @Valid @RequestBody EventRequest eventRequest, Authentication authentication) {
        UserWithId userDetails = (UserWithId) authentication.getPrincipal();

        Event event = eventService.updateEvent(eventId, eventRequest, userDetails);
        return GenericResponse.builder().error(false).event(modelMapper.map(event, EventResponse.class)).build();
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

        eventEntities.forEach(e -> eventResponses.add(modelMapper.map(e, EventResponse.class)));
        return GenericResponse.builder().error(false).events(eventResponses).build();
    }
}

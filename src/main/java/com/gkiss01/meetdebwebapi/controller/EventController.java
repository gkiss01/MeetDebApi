package com.gkiss01.meetdebwebapi.controller;

import com.gkiss01.meetdebwebapi.entity.Event;
import com.gkiss01.meetdebwebapi.model.EventRequest;
import com.gkiss01.meetdebwebapi.model.EventResponse;
import com.gkiss01.meetdebwebapi.model.SuccessResponse;
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
    public EventResponse createEvent(@RequestPart("event") @Valid EventRequest eventRequest,
                                       @RequestPart(value = "file", required = false) MultipartFile file,
                                       Authentication authentication) {
        UserWithId userDetails = (UserWithId) authentication.getPrincipal();

        Event event = eventService.createEvent(eventRequest, userDetails);
        if (file != null && !file.isEmpty())
            fileService.storeFile(event.getId(), file);
        return modelMapper.map(event, EventResponse.class);
    }

    @PreAuthorize("hasRole('CLIENT')")
    @PostMapping(path = "/update/{eventId}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE },
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public EventResponse updateEvent(@PathVariable Long eventId,
                                       @RequestPart("event") @Valid EventRequest eventRequest,
                                       @RequestPart(value = "file", required = false) MultipartFile file,
                                       Authentication authentication) {
        UserWithId userDetails = (UserWithId) authentication.getPrincipal();

        Event event = eventService.updateEvent(eventId, eventRequest, userDetails);
        return modelMapper.map(event, EventResponse.class);
    }

    @PreAuthorize("hasRole('CLIENT')")
    @DeleteMapping(path = "/{eventId}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public SuccessResponse<Long> deleteEvent(@PathVariable Long eventId, Authentication authentication) {
        UserWithId userDetails = (UserWithId) authentication.getPrincipal();

        eventService.deleteEvent(eventId, userDetails);
        fileService.deleteFile(eventId);
        return new SuccessResponse<>(eventId);
    }

    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping(path = "/{eventId}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public EventResponse getEvent(@PathVariable Long eventId, Authentication authentication) {
        UserWithId userDetails = (UserWithId) authentication.getPrincipal();

        Event event = eventService.getEvent(eventId, userDetails);
        return modelMapper.map(event, EventResponse.class);
    }

    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping(produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public List<EventResponse> getEvents(@RequestParam(value = "page", defaultValue = "0") int page,
                                     @RequestParam(value = "limit", defaultValue = "25") int limit,
                                     Authentication authentication) {
        UserWithId userDetails = (UserWithId) authentication.getPrincipal();

        if (page > 0) page--;
        List<Event> eventEntities = eventService.getEvents(page, limit, userDetails);
        List<EventResponse> eventResponses = new ArrayList<>();

        eventEntities.forEach(e -> eventResponses.add(modelMapper.map(e, EventResponse.class)));
        return eventResponses;
    }

    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping(path = "/reports-add/{eventId}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public SuccessResponse<Long> reportEvent(@PathVariable Long eventId) {
        eventService.reportEvent(eventId);
        return new SuccessResponse<>(eventId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path = "/reports-remove/{eventId}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public SuccessResponse<Long> removeReport(@PathVariable Long eventId) {
        eventService.removeReport(eventId);
        return new SuccessResponse<>(eventId);
    }
}

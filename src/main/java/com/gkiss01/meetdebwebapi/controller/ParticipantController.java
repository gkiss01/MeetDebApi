package com.gkiss01.meetdebwebapi.controller;

import com.gkiss01.meetdebwebapi.entity.Participant;
import com.gkiss01.meetdebwebapi.model.GenericResponse;
import com.gkiss01.meetdebwebapi.model.ParticipantResponse;
import com.gkiss01.meetdebwebapi.service.ParticipantService;
import com.gkiss01.meetdebwebapi.utils.UserWithId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("participants")
public class ParticipantController {

    @Autowired
    private ParticipantService participantService;

    @PreAuthorize("hasRole('CLIENT')")
    @PostMapping(path = "/{eventId}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public GenericResponse createParticipant(@PathVariable Long eventId, Authentication authentication) {
        UserWithId userDetails = (UserWithId) authentication.getPrincipal();

        Participant participant = participantService.createParticipant(eventId, userDetails);
        ParticipantResponse response = new ParticipantResponse(eventId, participant.getId().getUser().getId(), participant.getId().getUser().getName());
        return GenericResponse.builder().error(false).participant(response).build();
    }

    @PreAuthorize("hasRole('CLIENT')")
    @DeleteMapping(path = "/{eventId}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public GenericResponse deleteEvent(@PathVariable Long eventId, Authentication authentication) {
        UserWithId userDetails = (UserWithId) authentication.getPrincipal();

        participantService.deleteParticipant(eventId, userDetails);
        return GenericResponse.builder().error(false).message("Participant deleted!").build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(path = "/{eventId}/{userId}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public GenericResponse deleteEvent(@PathVariable Long eventId, @PathVariable Long userId) {
        participantService.deleteParticipant(eventId, userId);
        return GenericResponse.builder().error(false).message("Participant deleted!").build();
    }

    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping(path = "/{eventId}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public GenericResponse getParticipants(@PathVariable Long eventId,
                                           @RequestParam(value = "page", defaultValue = "0") int page,
                                           @RequestParam(value = "limit", defaultValue = "25") int limit) {

        if (page > 0) page--;
        List<Participant> participantEntities = participantService.getParticipants(eventId, page, limit);
        List<ParticipantResponse> participantResponses = new ArrayList<>();

        participantEntities.forEach(e -> {
            ParticipantResponse participantResponse = new ParticipantResponse(null, e.getId().getUser().getId(), e.getId().getUser().getName());
            participantResponses.add(participantResponse);
        });
        return GenericResponse.builder().error(false).participants(participantResponses).build();
    }
}

package com.gkiss01.meetdebwebapi.controller;

import com.gkiss01.meetdebwebapi.entity.Date;
import com.gkiss01.meetdebwebapi.model.GenericResponse;
import com.gkiss01.meetdebwebapi.service.VoteService;
import com.gkiss01.meetdebwebapi.utils.UserWithId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("votes")
public class VoteController {

    @Autowired
    private VoteService voteService;

    @PreAuthorize("hasRole('CLIENT')")
    @PostMapping(path = "/{dateId}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public GenericResponse createVote(@PathVariable Long dateId, Authentication authentication) {
        UserWithId userDetails = (UserWithId) authentication.getPrincipal();

        List<Date> dateEntities = voteService.createVote(dateId, userDetails);
        return GenericResponse.builder().error(false).dates(dateEntities).build();
    }

    @PreAuthorize("hasRole('CLIENT')")
    @DeleteMapping(path = "/{dateId}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public GenericResponse deleteVote(@PathVariable Long dateId, Authentication authentication) {
        UserWithId userDetails = (UserWithId) authentication.getPrincipal();

        Date date = voteService.deleteVote(dateId, userDetails);
        return GenericResponse.builder().error(false).date(date).build();
    }
}

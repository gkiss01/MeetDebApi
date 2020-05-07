package com.gkiss01.meetdebwebapi.controller;

import com.gkiss01.meetdebwebapi.entity.Date;
import com.gkiss01.meetdebwebapi.model.GenericResponse;
import com.gkiss01.meetdebwebapi.service.DateService;
import com.gkiss01.meetdebwebapi.utils.UserWithId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("dates")
public class DateController {

    @Autowired
    private DateService dateService;

    @PreAuthorize("hasRole('CLIENT')")
    @PostMapping(path = "/{eventId}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public GenericResponse createDate(@PathVariable Long eventId,
                                      @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime dateTime,
                                      Authentication authentication) {
        UserWithId userDetails = (UserWithId) authentication.getPrincipal();

        List<Date> dateEntities = dateService.createDate(eventId, dateTime, userDetails);
        return GenericResponse.builder().error(false).dates(dateEntities).build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(path = "/{id}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public GenericResponse deleteDate(@PathVariable Long id,
                                      @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime dateTime) {

        if (dateTime == null) dateService.deleteDate(id);
        else dateService.deleteDate(id, dateTime);
        return GenericResponse.builder().error(false).withId(id).message("Date deleted!").build();
    }

    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping(path = "/{eventId}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public GenericResponse getDates(@PathVariable Long eventId, Authentication authentication) {
        UserWithId userDetails = (UserWithId) authentication.getPrincipal();

        List<Date> dateEntities = dateService.getDates(eventId, userDetails);
        return GenericResponse.builder().error(false).dates(dateEntities).build();
    }
}

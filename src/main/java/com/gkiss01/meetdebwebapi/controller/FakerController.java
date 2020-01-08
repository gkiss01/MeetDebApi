package com.gkiss01.meetdebwebapi.controller;

import com.github.javafaker.Faker;
import com.gkiss01.meetdebwebapi.entity.Event;
import com.gkiss01.meetdebwebapi.entity.Participant;
import com.gkiss01.meetdebwebapi.entity.Role;
import com.gkiss01.meetdebwebapi.entity.User;
import com.gkiss01.meetdebwebapi.entity.idclass.ParticipantId;
import com.gkiss01.meetdebwebapi.model.GenericResponse;
import com.gkiss01.meetdebwebapi.repository.EventRepository;
import com.gkiss01.meetdebwebapi.repository.ParticipantRepository;
import com.gkiss01.meetdebwebapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.gkiss01.meetdebwebapi.entity.Role.ROLE_ADMIN;
import static com.gkiss01.meetdebwebapi.entity.Role.ROLE_CLIENT;

@RestController
@RequestMapping("faker")
public class FakerController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path = "/users/{count}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public GenericResponse populateUsers(@PathVariable Long count) {

        Faker faker = new Faker(new Locale("hu"));
        Set<Role> roles;

        for (long i = 0; i < count; ++i) {
            if (faker.number().randomDigit() > 1) roles = new HashSet<>(Collections.singletonList(ROLE_CLIENT));
            else roles = new HashSet<>(Arrays.asList(ROLE_CLIENT, ROLE_ADMIN));

            User user = new User(i, faker.internet().emailAddress(), bCryptPasswordEncoder.encode(faker.internet().password()), faker.name().fullName(), true, roles);
            user.setId(null);

            userRepository.save(user);
        }
        return GenericResponse.builder().error(false).message(count + " user(s) added!").build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path = "/events/{count}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public GenericResponse populateEvents(@PathVariable Long count) {

        Faker faker = new Faker(new Locale("hu"));

        for (long i = 0; i < count; ++i) {
            User user = userRepository.findUserByRandom();
            if (user == null)
                throw new RuntimeException("No users found!");

            Event event = new Event(i, faker.date().future(31, TimeUnit.DAYS).toInstant().atOffset(ZoneOffset.UTC), faker.address().fullAddress(), faker.lorem().sentence(), user.getId(), null, null, null);
            event.setId(null);

            eventRepository.save(event);
        }
        return GenericResponse.builder().error(false).message(count + " event(s) added!").build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path = "/participants/{count}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public GenericResponse populateParticipants(@PathVariable Long count) {

        for (long i = 0; i < count; ++i) {
            Event event = eventRepository.findEventByRandom();
            if (event == null)
                throw new RuntimeException("No event found!");

            User user = userRepository.findUserByRandom();
            if (user == null)
                throw new RuntimeException("No users found!");

            Participant participant = new Participant(new ParticipantId(event, user));

            participantRepository.save(participant);
        }
        return GenericResponse.builder().error(false).message(count + " participant(s) added!").build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path = "/participants/{eventId}/{count}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public GenericResponse populateParticipantsByEvent(@PathVariable Long eventId, @PathVariable Long count) {

        Event event = eventRepository.findEventById(eventId);
        if (event == null)
            throw new RuntimeException("Event not found!");

        for (long i = 0; i < count; ++i) {
            User user = userRepository.findUserByRandom();
            if (user == null)
                throw new RuntimeException("No users found!");

            Participant participant = new Participant(new ParticipantId(event, user));

            participantRepository.save(participant);
        }
        return GenericResponse.builder().error(false).message(count + " participant(s) added!").build();
    }
}

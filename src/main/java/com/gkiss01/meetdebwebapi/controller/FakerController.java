package com.gkiss01.meetdebwebapi.controller;

import com.github.javafaker.Faker;
import com.gkiss01.meetdebwebapi.entity.*;
import com.gkiss01.meetdebwebapi.entity.idclass.ParticipantId;
import com.gkiss01.meetdebwebapi.entity.idclass.VoteId;
import com.gkiss01.meetdebwebapi.model.GenericResponse;
import com.gkiss01.meetdebwebapi.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZoneOffset;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

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
    private DateRepository dateRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path = "/users/{count}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public GenericResponse populateUsers(@PathVariable Long count) {

        Faker faker = new Faker(new Locale("hu"));

        for (long i = 0; i < count; ++i) {
            User user = new User(i, faker.internet().emailAddress(), bCryptPasswordEncoder.encode(faker.internet().password()), faker.name().fullName(), true, new HashSet<>(Collections.singletonList(ROLE_CLIENT)));
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
            Long userId = userRepository.findUserIdByRandom();
            if (userId == null)
                throw new RuntimeException("No users found!");

            Event event = new Event(i, faker.date().future(31, TimeUnit.DAYS).toInstant().atOffset(ZoneOffset.UTC), faker.address().fullAddress(), faker.lorem().paragraph(5), userId, null, null, null);
            event.setId(null);

            eventRepository.save(event);
        }
        return GenericResponse.builder().error(false).message(count + " event(s) added!").build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path = "/participants/{count}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public GenericResponse populateParticipants(@PathVariable Long count) {

        for (long i = 0; i < count; ++i) {
            Long eventId = eventRepository.findEventIdByRandom();
            if (eventId == null)
                throw new RuntimeException("No events found!");

            Long userId = userRepository.findUserIdByRandom();
            if (userId == null)
                throw new RuntimeException("No users found!");

            Participant participant = new Participant(new ParticipantId(eventId, userId), null);

            participantRepository.save(participant);
        }
        return GenericResponse.builder().error(false).message(count + " participant(s) added!").build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path = "/participants/{eventId}/{count}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public GenericResponse populateParticipantsByEvent(@PathVariable Long eventId, @PathVariable Long count) {

        if (!eventRepository.existsEventById(eventId))
            throw new RuntimeException("Event not found!");

        for (long i = 0; i < count; ++i) {
            Long userId = userRepository.findUserIdByRandom();
            if (userId == null)
                throw new RuntimeException("No users found!");

            Participant participant = new Participant(new ParticipantId(eventId, userId), null);

            participantRepository.save(participant);
        }
        return GenericResponse.builder().error(false).message(count + " participant(s) added!").build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path = "/dates/{count}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public GenericResponse populateDates(@PathVariable Long count) {

        Faker faker = new Faker(new Locale("hu"));

        for (long i = 0; i < count; ++i) {
            Long eventId = eventRepository.findEventIdByRandom();
            if (eventId == null)
                throw new RuntimeException("No events found!");

            Date date = new Date(eventId, faker.date().future(31, TimeUnit.DAYS).toInstant().atOffset(ZoneOffset.UTC));

            dateRepository.save(date);
        }
        return GenericResponse.builder().error(false).message(count + " date(s) added!").build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path = "/dates/{eventId}/{count}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public GenericResponse populateDatesByEvent(@PathVariable Long eventId, @PathVariable Long count) {

        if (!eventRepository.existsEventById(eventId))
            throw new RuntimeException("Event not found!");

        Faker faker = new Faker(new Locale("hu"));

        for (long i = 0; i < count; ++i) {
            Date date = new Date(eventId, faker.date().future(31, TimeUnit.DAYS).toInstant().atOffset(ZoneOffset.UTC));

            dateRepository.save(date);
        }
        return GenericResponse.builder().error(false).message(count + " date(s) added!").build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path = "/votes/{count}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public GenericResponse populateVotes(@PathVariable Long count) {

        for (long i = 0; i < count; ++i) {
            Long dateId = dateRepository.findDateIdByRandom();
            if (dateId == null)
                throw new RuntimeException("No dates found!");

            Long userId = userRepository.findUserIdByRandom();
            if (userId == null)
                throw new RuntimeException("No users found!");

            Vote vote = new Vote(new VoteId(dateId, userId));

            voteRepository.save(vote);
        }
        return GenericResponse.builder().error(false).message(count + " vote(s) added!").build();
    }
}

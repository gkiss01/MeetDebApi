package com.gkiss01.meetdebwebapi.controller;

import com.github.javafaker.Faker;
import com.gkiss01.meetdebwebapi.entity.*;
import com.gkiss01.meetdebwebapi.entity.idclass.ParticipantId;
import com.gkiss01.meetdebwebapi.entity.idclass.VoteId;
import com.gkiss01.meetdebwebapi.repository.*;
import com.gkiss01.meetdebwebapi.utils.CustomRuntimeException;
import com.gkiss01.meetdebwebapi.utils.ErrorCodes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static com.gkiss01.meetdebwebapi.entity.Role.ROLE_CLIENT;

@RestController
@RequestMapping("faker")
public class FakerController {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final ParticipantRepository participantRepository;
    private final DateRepository dateRepository;
    private final VoteRepository voteRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public FakerController(UserRepository userRepository, EventRepository eventRepository, ParticipantRepository participantRepository, DateRepository dateRepository, VoteRepository voteRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.participantRepository = participantRepository;
        this.dateRepository = dateRepository;
        this.voteRepository = voteRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path = "/users/{count}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public String populateUsers(@PathVariable Long count) {

        Faker faker = new Faker(new Locale("hu"));

        for (long i = 0; i < count; ++i) {
            User user = new User(i, faker.internet().emailAddress(), bCryptPasswordEncoder.encode(faker.internet().password()), faker.name().fullName(), true, new HashSet<>(Collections.singletonList(ROLE_CLIENT)));
            user.setId(null);
            userRepository.save(user);
        }
        return count + " user(s) added!";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path = "/events/{count}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public String populateEvents(@PathVariable Long count) {

        Faker faker = new Faker(new Locale("hu"));

        for (long i = 0; i < count; ++i) {
            Long userId = userRepository.findUserIdByRandom();
            if (userId == null)
                throw new CustomRuntimeException(ErrorCodes.NO_USERS_FOUND);

            OffsetDateTime offsetDateTime = faker.date().future(31, TimeUnit.DAYS).toInstant().atOffset(ZoneOffset.UTC);

            Event event = new Event(i, faker.funnyName().name(), offsetDateTime, faker.address().fullAddress(), faker.lorem().paragraph(5), false, userId, null, null, null, null);
            event.setId(null);
            eventRepository.save(event);
        }
        return count + " event(s) added!";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path = "/participants/{eventId}/{count}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public String populateParticipantsByEvent(@PathVariable Long eventId, @PathVariable Long count) {

        if (!eventRepository.existsEventById(eventId))
            throw new CustomRuntimeException(ErrorCodes.EVENT_NOT_FOUND);

        for (long i = 0; i < count; ++i) {
            Long userId = userRepository.findUserIdByRandom();
            if (userId == null)
                throw new CustomRuntimeException(ErrorCodes.NO_USERS_FOUND);

            if (participantRepository.existsParticipantById_EventIdAndId_UserId(eventId, userId)) continue;

            Participant participant = new Participant(new ParticipantId(eventId, userId), null);
            participantRepository.save(participant);
        }
        return count + " participant(s) for event " + eventId + " added!";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path = "/participants/{count}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public String populateParticipants(@PathVariable Long count) {

        for (long i = 0; i < count; ++i) {
            Long userId = userRepository.findUserIdByRandom();
            if (userId == null)
                throw new CustomRuntimeException(ErrorCodes.NO_USERS_FOUND);

            Long eventId = eventRepository.findEventIdByRandom();
            if (eventId == null)
                throw new CustomRuntimeException(ErrorCodes.NO_EVENTS_FOUND);

            if (participantRepository.existsParticipantById_EventIdAndId_UserId(eventId, userId)) continue;

            Participant participant = new Participant(new ParticipantId(eventId, userId), null);
            participantRepository.save(participant);
        }
        return count + " participant(s) added!";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path = "/dates/{eventId}/{count}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public String populateDatesByEvent(@PathVariable Long eventId, @PathVariable Long count) {

        if (!eventRepository.existsEventById(eventId))
            throw new CustomRuntimeException(ErrorCodes.EVENT_NOT_FOUND);

        Faker faker = new Faker(new Locale("hu"));

        for (long i = 0; i < count; ++i) {
            OffsetDateTime offsetDateTime = faker.date().future(31, TimeUnit.DAYS).toInstant().atOffset(ZoneOffset.UTC);
            if (dateRepository.existsByEventIdAndDate(eventId, offsetDateTime)) continue;

            Date date = new Date(eventId, offsetDateTime);
            dateRepository.save(date);
        }
        return count + " date(s) for event " + eventId + " added!";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path = "/dates/{count}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public String populateDates(@PathVariable Long count) {

        Faker faker = new Faker(new Locale("hu"));

        for (long i = 0; i < count; ++i) {
            Long eventId = eventRepository.findEventIdByRandom();
            if (eventId == null)
                throw new CustomRuntimeException(ErrorCodes.NO_EVENTS_FOUND);

            OffsetDateTime offsetDateTime = faker.date().future(31, TimeUnit.DAYS).toInstant().atOffset(ZoneOffset.UTC);
            if (dateRepository.existsByEventIdAndDate(eventId, offsetDateTime)) continue;

            Date date = new Date(eventId, offsetDateTime);
            dateRepository.save(date);
        }
        return count + " date(s) added!";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path = "/votes/{count}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public String populateVotes(@PathVariable Long count) {

        for (long i = 0; i < count; ++i) {
            Long dateId = dateRepository.findDateIdByRandom();
            if (dateId == null)
                throw new CustomRuntimeException(ErrorCodes.NO_DATES_FOUND);

            Long userId = userRepository.findUserIdByRandom();
            if (userId == null)
                throw new CustomRuntimeException(ErrorCodes.NO_USERS_FOUND);

            if (voteRepository.existsById_DateIdAndId_UserId(dateId, userId)) continue;

            Vote vote = new Vote(new VoteId(dateId, userId));
            voteRepository.save(vote);
        }
        return count + " vote(s) added!";
    }
}

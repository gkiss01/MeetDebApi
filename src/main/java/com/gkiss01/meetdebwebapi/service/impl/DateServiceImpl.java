package com.gkiss01.meetdebwebapi.service.impl;

import com.gkiss01.meetdebwebapi.entity.Date;
import com.gkiss01.meetdebwebapi.repository.DateRepository;
import com.gkiss01.meetdebwebapi.repository.EventRepository;
import com.gkiss01.meetdebwebapi.repository.VoteRepository;
import com.gkiss01.meetdebwebapi.service.DateService;
import com.gkiss01.meetdebwebapi.utils.CustomRuntimeException;
import com.gkiss01.meetdebwebapi.utils.ErrorCodes;
import com.gkiss01.meetdebwebapi.utils.UserWithId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class DateServiceImpl implements DateService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private DateRepository dateRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Override
    public List<Date> createDate(Long eventId, OffsetDateTime dateTime, UserWithId userDetails) {
        if (!eventRepository.existsEventById(eventId))
            throw new CustomRuntimeException(ErrorCodes.EVENT_NOT_FOUND);

        if (dateRepository.existsByEventIdAndDate(eventId, dateTime))
            throw new CustomRuntimeException(ErrorCodes.DATE_ALREADY_CREATED);

        Date date = new Date(eventId, dateTime);
        dateRepository.save(date);
        return dateRepository.findDateByEventIdOrderByDateCustom(eventId, userDetails.getUserId());
    }

    @Override
    @Transactional
    public void deleteDate(Long eventId, OffsetDateTime dateTime) {
        Date date = dateRepository.findByEventIdAndDate(eventId, dateTime);

        if (date == null)
            throw new CustomRuntimeException(ErrorCodes.DATE_NOT_FOUND);

        voteRepository.deleteById_DateId(date.getId());
        dateRepository.delete(date);
    }

    @Override
    @Transactional
    public void deleteDate(Long dateId) {
        Date date = dateRepository.findDateById(dateId);

        if (date == null)
            throw new CustomRuntimeException(ErrorCodes.DATE_NOT_FOUND);

        voteRepository.deleteById_DateId(dateId);
        dateRepository.delete(date);
    }

    @Override
    public List<Date> getDates(Long eventId, UserWithId userDetails) {
        if (!eventRepository.existsEventById(eventId))
            throw new CustomRuntimeException(ErrorCodes.EVENT_NOT_FOUND);

        return dateRepository.findDateByEventIdOrderByDateCustom(eventId, userDetails.getUserId());
    }
}
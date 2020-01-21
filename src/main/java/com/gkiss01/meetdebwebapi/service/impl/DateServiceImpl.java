package com.gkiss01.meetdebwebapi.service.impl;

import com.gkiss01.meetdebwebapi.entity.Date;
import com.gkiss01.meetdebwebapi.repository.DateRepository;
import com.gkiss01.meetdebwebapi.repository.EventRepository;
import com.gkiss01.meetdebwebapi.service.DateService;
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

    @Override
    public Date createDate(Long eventId, OffsetDateTime dateTime) {
        if (!eventRepository.existsEventById(eventId))
            throw new RuntimeException("Event not found!");

        if (dateRepository.existsByEventIdAndDate(eventId, dateTime))
            throw new RuntimeException("Date is already created!");

        Date date = new Date(eventId, dateTime);
        return dateRepository.save(date);
    }

    @Override
    @Transactional
    public void deleteDate(Long eventId, OffsetDateTime dateTime) {
        Date date = dateRepository.findByEventIdAndDate(eventId, dateTime);

        if (date == null)
            throw new RuntimeException("Date not found!");

        dateRepository.delete(date);
    }

    @Override
    @Transactional
    public void deleteDate(Long dateId) {
        Date date = dateRepository.findDateById(dateId);

        if (date == null)
            throw new RuntimeException("Date not found!");

        dateRepository.delete(date);
    }

    @Override
    public List<Date> getDates(Long eventId) {
        if (!eventRepository.existsEventById(eventId))
            throw new RuntimeException("Event not found!");

        return dateRepository.findByEventId(eventId);
    }
}
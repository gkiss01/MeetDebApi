package com.gkiss01.meetdebwebapi.service;

import com.gkiss01.meetdebwebapi.entity.Date;

import java.time.OffsetDateTime;
import java.util.List;

public interface DateService {
    Date createDate(Long eventId, OffsetDateTime dateTime);
    void deleteDate(Long eventId, OffsetDateTime dateTime);
    void deleteDate(Long dateId);
    List<Date> getDates(Long eventId);
}

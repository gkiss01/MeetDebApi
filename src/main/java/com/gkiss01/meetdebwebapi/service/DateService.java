package com.gkiss01.meetdebwebapi.service;

import com.gkiss01.meetdebwebapi.entity.Date;
import com.gkiss01.meetdebwebapi.utils.UserWithId;

import java.time.OffsetDateTime;
import java.util.List;

public interface DateService {
    List<Date> createDate(Long eventId, OffsetDateTime dateTime, UserWithId userDetails);
    void deleteDate(Long dateId);
    List<Date> getDates(Long eventId, UserWithId userDetails);
}

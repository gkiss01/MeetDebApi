package com.gkiss01.meetdebwebapi.repository;

import com.gkiss01.meetdebwebapi.entity.Date;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

public interface DateRepository extends JpaRepository<Date, Long> {

    Boolean existsByEventIdAndDate(Long eventId, OffsetDateTime dateTime);

    Date findByEventIdAndDate(Long eventId, OffsetDateTime dateTime);

    Date findDateById(Long dateId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Date d WHERE d.eventId IN (SELECT e.id FROM Event e WHERE e.userId = :userId)")
    void deleteDatesByEventCreator(@Param("userId") Long userId);

    void deleteByEventId(Long eventId);

    List<Date> findByEventId(Long eventId);
}

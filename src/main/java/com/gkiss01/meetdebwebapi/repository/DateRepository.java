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

    @Query("SELECT NEW com.gkiss01.meetdebwebapi.entity.Date(d.id, d.eventId, d.date,\n" +
            "(SELECT COUNT(v) FROM Vote v WHERE v.id.dateId = d.id) AS votes,\n" +
            "CASE WHEN (SELECT v.id.userId FROM Vote v WHERE v.id.userId = :userId and v.id.dateId = d.id) is not null THEN true ELSE false END AS accepted)\n" +
            "FROM Date d WHERE d.id = :dateId")
    Date findDateByIdCustom(@Param("dateId") Long dateId, @Param("userId") Long userId);

    @Query("SELECT NEW com.gkiss01.meetdebwebapi.entity.Date(d.id, d.eventId, d.date,\n" +
            "(SELECT COUNT(v) FROM Vote v WHERE v.id.dateId = d.id) AS votes,\n" +
            "CASE WHEN (SELECT v.id.userId FROM Vote v WHERE v.id.userId = :userId and v.id.dateId = d.id) is not null THEN true ELSE false END AS accepted)\n" +
            "FROM Date d WHERE d.eventId = :eventId ORDER BY d.date")
    List<Date> findDateByEventIdOrderByDateCustom(@Param("eventId") Long eventId, @Param("userId") Long userId);

    Date findDateById(Long dateId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Date d WHERE d.eventId IN (SELECT e.id FROM Event e WHERE e.userId = :userId)")
    void deleteDatesByEventCreator(@Param("userId") Long userId);

    void deleteByEventId(Long eventId);
}

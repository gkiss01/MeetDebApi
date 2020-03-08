package com.gkiss01.meetdebwebapi.repository;

import com.gkiss01.meetdebwebapi.entity.Event;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT NEW com.gkiss01.meetdebwebapi.entity.Event(e.id, e.name, e.date, e.venue, e.description, e.reported, e.userId, (SELECT u.name FROM User u WHERE u.id = e.userId) AS username,\n" +
            "(SELECT COUNT(p) FROM Participant p WHERE p.id.eventId = e.id) AS participants,\n" +
            "CASE WHEN (SELECT p.id.userId FROM Participant p WHERE p.id.userId = :userId and p.id.eventId = e.id) is not null THEN true ELSE false END AS accepted,\n" +
            "CASE WHEN (SELECT d.eventId FROM Date d WHERE d.eventId = e.id AND d.id IN (SELECT v.id.dateId FROM Vote v WHERE v.id.userId = :userId)) is not null THEN true ELSE false END AS voted)\n" +
            "FROM Event e WHERE e.id = :eventId")
    Event findEventByIdCustom(@Param("eventId") Long eventId, @Param("userId") Long userId);

    @Query("SELECT NEW com.gkiss01.meetdebwebapi.entity.Event(e.id, e.name, e.date, e.venue, e.description, e.reported, e.userId, (SELECT u.name FROM User u WHERE u.id = e.userId) AS username,\n" +
            "(SELECT COUNT(p) FROM Participant p WHERE p.id.eventId = e.id) AS participants,\n" +
            "CASE WHEN (SELECT p.id.userId FROM Participant p WHERE p.id.userId = :userId and p.id.eventId = e.id) is not null THEN true ELSE false END AS accepted,\n" +
            "CASE WHEN (SELECT d.eventId FROM Date d WHERE d.eventId = e.id AND d.id IN (SELECT v.id.dateId FROM Vote v WHERE v.id.userId = :userId)) is not null THEN true ELSE false END AS voted)\n" +
            "FROM Event e ORDER BY e.date")
    List<Event> findAllByOrderByDateCustom(@Param("userId") Long userId, Pageable pageable);

    Event findEventById(Long eventId);

    Boolean existsEventById(Long eventId);

    void deleteByUserId(Long userId);

    @Query(value = "SELECT id FROM events ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Long findEventIdByRandom();
}

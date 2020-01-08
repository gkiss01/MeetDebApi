package com.gkiss01.meetdebwebapi.repository;

import com.gkiss01.meetdebwebapi.entity.Event;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT NEW com.gkiss01.meetdebwebapi.entity.Event(e.id, e.date, e.venue, e.labels, e.userId, (SELECT u.name FROM User u WHERE u.id = e.userId) AS username,\n" +
            "(SELECT COUNT(p) FROM Participant p WHERE p.id.event.id = e.id) AS participants,\n" +
            "CASE WHEN (SELECT p.id.user.id FROM Participant p WHERE p.id.user.id = :userId and p.id.event.id = e.id) is not null THEN true ELSE false END AS accepted)\n" +
            "FROM Event e WHERE e.id = :eventId")
    Event findEventByIdCustom(@Param("eventId") Long eventId, @Param("userId") Long userId);

    @Query("SELECT NEW com.gkiss01.meetdebwebapi.entity.Event(e.id, e.date, e.venue, e.labels, e.userId, (SELECT u.name FROM User u WHERE u.id = e.userId) AS username,\n" +
            "(SELECT COUNT(p) FROM Participant p WHERE p.id.event.id = e.id) AS participants,\n" +
            "CASE WHEN (SELECT p.id.user.id FROM Participant p WHERE p.id.user.id = :userId and p.id.event.id = e.id) is not null THEN true ELSE false END AS accepted)\n" +
            "FROM Event e ORDER BY e.date")
    List<Event> findAllByOrderByDateCustom(@Param("userId") Long userId, Pageable pageable);

    Event findEventById(Long eventId);

    Boolean existsEventById(Long eventId);

    void deleteByUserId(Long userId);

    @Query(value = "SELECT * FROM events ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Event findEventByRandom();
}

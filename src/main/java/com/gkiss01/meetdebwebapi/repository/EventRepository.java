package com.gkiss01.meetdebwebapi.repository;

import com.gkiss01.meetdebwebapi.entity.Event;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    Event findEventById(Long eventId);

    List<Event> findAllByOrderByDate(Pageable pageable);

    void deleteByUserId(Long userId);

    @Query(value = "SELECT * FROM events ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Event findEventByRandom();
}

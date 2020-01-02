package com.gkiss01.meetdebwebapi.repository;

import com.gkiss01.meetdebwebapi.entity.Event;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    Event findEventById(Long eventId);

    List<Event> findAllByOrderByDate(Pageable pageable);

    void deleteByUserId(Long userId);
}

package com.gkiss01.meetdebwebapi.repository;

import com.gkiss01.meetdebwebapi.entity.Participant;
import com.gkiss01.meetdebwebapi.entity.idclass.ParticipantId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ParticipantRepository extends JpaRepository<Participant, ParticipantId> {

    Participant findParticipantById_EventIdAndId_UserId(Long eventId, Long userId);

    @Query("SELECT NEW com.gkiss01.meetdebwebapi.entity.Participant(p.id, (SELECT u.name FROM User u WHERE u.id = p.id.userId) AS username) FROM Participant p WHERE p.id.eventId = :eventId")
    List<Participant> findParticipantById_EventIdCustom(@Param("eventId") Long eventId, Pageable pageable);

    @Transactional
    @Modifying
    @Query("DELETE FROM Participant p WHERE p.id.eventId IN (SELECT e.id FROM Event e WHERE e.userId = :userId)")
    void deleteParticipantsByEventCreator(@Param("userId") Long userId);

    void deleteById_EventId(Long eventId);

    void deleteById_UserId(Long userId);

    void deleteById_EventIdAndId_UserId(Long eventId, Long userId);
}

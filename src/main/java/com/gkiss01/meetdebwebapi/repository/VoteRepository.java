package com.gkiss01.meetdebwebapi.repository;

import com.gkiss01.meetdebwebapi.entity.Vote;
import com.gkiss01.meetdebwebapi.entity.idclass.VoteId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface VoteRepository extends JpaRepository<Vote, VoteId> {

    Boolean existsById_DateIdAndId_UserId(Long dateId, Long userId);

    Vote findVoteById_DateIdAndId_UserId(Long dateId, Long userId);

    void deleteById_DateId(Long dateId);

    void deleteById_UserId(Long userId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Vote v WHERE v.id.userId = :userId AND v.id.dateId IN (SELECT d.id FROM Date d WHERE d.eventId = :eventId)")
    void deleteByEventIdAndUserId(@Param("eventId") Long eventId, @Param("userId") Long userId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Vote v WHERE v.id.dateId IN (SELECT d.id FROM Date d WHERE d.eventId = :eventId)")
    void deleteVotesByDateEvent(@Param("eventId") Long eventId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Vote v WHERE v.id.dateId IN (SELECT d.id FROM Date d WHERE d.eventId IN (SELECT e.id FROM Event e WHERE e.userId = :userId))")
    void deleteVotesByDateEventCreator(@Param("userId") Long userId);
}

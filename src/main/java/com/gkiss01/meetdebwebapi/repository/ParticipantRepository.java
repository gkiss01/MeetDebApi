package com.gkiss01.meetdebwebapi.repository;

import com.gkiss01.meetdebwebapi.entity.Participant;
import com.gkiss01.meetdebwebapi.entity.idclass.ParticipantId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParticipantRepository extends JpaRepository<Participant, ParticipantId> {

    List<Participant> findById_Event_Id(Long eventId, Pageable pageable);
}
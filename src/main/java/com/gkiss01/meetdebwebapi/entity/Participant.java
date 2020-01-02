package com.gkiss01.meetdebwebapi.entity;

import com.gkiss01.meetdebwebapi.entity.idclass.ParticipantId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Participants")
public class Participant {
    @EmbeddedId
    private ParticipantId id;
}
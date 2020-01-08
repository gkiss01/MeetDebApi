package com.gkiss01.meetdebwebapi.entity.idclass;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ParticipantId implements Serializable {
    @Column(nullable = false)
    private Long eventId;

    @Column(nullable = false)
    private Long userId;
}

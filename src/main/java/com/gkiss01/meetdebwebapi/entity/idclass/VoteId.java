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
public class VoteId implements Serializable {
    @Column(nullable = false)
    private Long dateId;

    @Column(nullable = false)
    private Long userId;
}

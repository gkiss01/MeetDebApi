package com.gkiss01.meetdebwebapi.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Event_Dates")
public class Date {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long eventId;

    @Column(nullable = false)
    private OffsetDateTime date;

    public Date(Long eventId, OffsetDateTime date) {
        this.eventId = eventId;
        this.date = date;
    }
}
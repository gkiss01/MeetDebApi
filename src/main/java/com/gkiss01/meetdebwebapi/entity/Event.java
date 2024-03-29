package com.gkiss01.meetdebwebapi.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Name is required!")
    private String name;

    @Column(nullable = false)
    private OffsetDateTime date;

    @Column(nullable = false)
    @NotBlank(message = "Venue is required!")
    private String venue;

    @Column(nullable = false)
    @Type(type = "text")
    @NotBlank(message = "Description is required!")
    private String description;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean reported = false;

    @Column(nullable = false)
    private Long userId;

    @Transient
    private String username = "";

    @Transient
    private Long participants = 0L;

    @Transient
    private Boolean accepted = false;

    @Transient
    private Boolean voted = false;
}
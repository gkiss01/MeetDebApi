package com.gkiss01.meetdebwebapi.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "User")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 120)
    private String email;

    @Column(nullable = false)
    @Size(min = 8, message = "Minimum password length is 8 characters!")
    private String password;

    @Size(min = 4, max = 80, message = "Name must be between 4 and 80 characters long!")
    @Column(nullable = false, length = 80)
    private String name;

    @Column(nullable = false)
    private Boolean enabled;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;
}
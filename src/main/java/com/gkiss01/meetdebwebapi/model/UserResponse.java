package com.gkiss01.meetdebwebapi.model;

import com.gkiss01.meetdebwebapi.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;

    private String email;

    private String name;

    private Boolean enabled;

    private Set<Role> roles;
}

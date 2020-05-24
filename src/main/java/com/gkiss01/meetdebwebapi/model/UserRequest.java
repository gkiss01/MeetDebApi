package com.gkiss01.meetdebwebapi.model;

import com.gkiss01.meetdebwebapi.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    @NotBlank(message = "Email is required!")
    @Email(message = "Email is not valid!")
    private String email;

    @NotBlank(message = "Password is required!")
    @Size(min = 8, message = "Minimum password length is 8 characters!")
    private String password;

    @NotBlank(message = "Name is required!")
    @Size(min = 4, max = 80, message = "Name must be between 4 and 80 characters long!")
    private String name;

    //private Integer type;

    //@NotNull(message = "Roles are required!")
    private Set<Role> roles;
}

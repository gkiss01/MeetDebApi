package com.gkiss01.meetdebwebapi.controller;

import com.gkiss01.meetdebwebapi.entity.User;
import com.gkiss01.meetdebwebapi.model.GenericResponse;
import com.gkiss01.meetdebwebapi.model.UserRequest;
import com.gkiss01.meetdebwebapi.model.UserResponse;
import com.gkiss01.meetdebwebapi.service.UserService;
import com.gkiss01.meetdebwebapi.utils.CustomRuntimeException;
import com.gkiss01.meetdebwebapi.utils.ErrorCodes;
import com.gkiss01.meetdebwebapi.utils.UserWithId;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

import static com.gkiss01.meetdebwebapi.entity.Role.ROLE_ADMIN;

@RestController
@RequestMapping("users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE },
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public GenericResponse createUser(@Valid @RequestBody UserRequest userRequest) {
        User user = userService.createUser(userRequest);
        return GenericResponse.builder().error(false).user(modelMapper.map(user, UserResponse.class)).build();
    }

    @PreAuthorize("hasRole('CLIENT')")
    @PutMapping(path = "/{userId}", consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE },
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public GenericResponse updateUser(@PathVariable Long userId, @Valid @RequestBody UserRequest userRequest, Authentication authentication) {
        UserWithId userDetails = (UserWithId) authentication.getPrincipal();

        if (!userDetails.getAuthorities().contains(ROLE_ADMIN) && !userDetails.getUserId().equals(userId))
            throw new CustomRuntimeException(ErrorCodes.ACCESS_DENIED);

        User user = userService.updateUser(userId, userRequest);
        return GenericResponse.builder().error(false).user(modelMapper.map(user, UserResponse.class)).build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(path = "/{userId}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public GenericResponse deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return GenericResponse.builder().error(false).message("User deleted!").build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path = "/{userId}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public GenericResponse getUser(@PathVariable Long userId) {
        User user = userService.getUser(userId);
        return GenericResponse.builder().error(false).user(modelMapper.map(user, UserResponse.class)).build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public GenericResponse getUsers(@RequestParam(value = "page", defaultValue = "0") int page,
                                       @RequestParam(value = "limit", defaultValue = "25") int limit) {
        if (page > 0) page--;
        List<User> userEntities = userService.getUsers(page, limit);
        List<UserResponse> userResponses = new ArrayList<>();

        userEntities.forEach(u -> {
            UserResponse userResponse = modelMapper.map(u, UserResponse.class);
            userResponses.add(userResponse);
        });
        return GenericResponse.builder().error(false).users(userResponses).build();
    }

    @GetMapping(path = "/confirm-account", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public GenericResponse confirmUser(@RequestParam("token") String token) {
        userService.confirmUser(token);
        return GenericResponse.builder().error(false).message("User verified!").build();
    }
}

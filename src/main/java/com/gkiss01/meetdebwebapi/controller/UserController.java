package com.gkiss01.meetdebwebapi.controller;

import com.gkiss01.meetdebwebapi.entity.User;
import com.gkiss01.meetdebwebapi.model.GenericResponse;
import com.gkiss01.meetdebwebapi.model.UserRequest;
import com.gkiss01.meetdebwebapi.model.UserResponse;
import com.gkiss01.meetdebwebapi.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

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
        return new GenericResponse(false, modelMapper.map(user, UserResponse.class));
    }

    @PutMapping(path = "/{userId}", consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE },
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public GenericResponse updateUser(@PathVariable Long userId, @Valid @RequestBody UserRequest userRequest) {
        User user = userService.updateUser(userId, userRequest);
        return new GenericResponse(false, modelMapper.map(user, UserResponse.class));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(path = "/{userId}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public GenericResponse deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return new GenericResponse(false, null, null, "User deleted!");
    }

    @GetMapping(path = "/{userId}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public GenericResponse getUser(@PathVariable Long userId) {
        User user = userService.getUser(userId);
        return new GenericResponse(false, modelMapper.map(user, UserResponse.class));
    }

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
        return new GenericResponse(false, null, userResponses);
    }
}

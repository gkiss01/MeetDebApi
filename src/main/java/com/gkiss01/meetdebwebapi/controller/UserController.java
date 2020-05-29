package com.gkiss01.meetdebwebapi.controller;

import com.gkiss01.meetdebwebapi.entity.User;
import com.gkiss01.meetdebwebapi.model.SuccessResponse;
import com.gkiss01.meetdebwebapi.model.UserRequest;
import com.gkiss01.meetdebwebapi.model.UserResponse;
import com.gkiss01.meetdebwebapi.service.UserService;
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

@RestController
@RequestMapping("users")
public class UserController {

    private final UserService userService;
    private final ModelMapper modelMapper;

    @Autowired
    public UserController(UserService userService, ModelMapper modelMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE },
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public UserResponse createUser(@Valid @RequestBody UserRequest userRequest) {
        User user = userService.createUser(userRequest);
        return modelMapper.map(user, UserResponse.class);
    }

    @GetMapping(path = "/confirm", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public UserResponse confirmUser(@RequestParam("token") String token) {
        User user = userService.confirmUser(token);
        return modelMapper.map(user, UserResponse.class);
    }

    @PreAuthorize("hasRole('CLIENT')")
    @PutMapping(consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE },
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public UserResponse updateUser(@RequestBody UserRequest userRequest, Authentication authentication) {
        UserWithId userDetails = (UserWithId) authentication.getPrincipal();

        User user = userService.updateUser(userDetails.getUserId(), userRequest, userDetails);
        return modelMapper.map(user, UserResponse.class);
    }

    @PreAuthorize("hasRole('CLIENT')")
    @DeleteMapping(produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public SuccessResponse<Long> deleteUser(Authentication authentication) {
        UserWithId userDetails = (UserWithId) authentication.getPrincipal();

        userService.deleteUser(userDetails.getUserId());
        return new SuccessResponse<>(userDetails.getUserId());
    }

    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping(path = "/me", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public UserResponse checkUser(Authentication authentication) {
        UserWithId userDetails = (UserWithId) authentication.getPrincipal();

        User user = userService.getUser(userDetails.getUserId());
        return modelMapper.map(user, UserResponse.class);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(path = "/{userId}", consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE },
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public UserResponse updateUser(@PathVariable Long userId, @RequestBody UserRequest userRequest, Authentication authentication) {
        UserWithId userDetails = (UserWithId) authentication.getPrincipal();

        User user = userService.updateUser(userId, userRequest, userDetails);
        return modelMapper.map(user, UserResponse.class);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(path = "/{userId}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public SuccessResponse<Long> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return new SuccessResponse<>(userId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path = "/{userId}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public UserResponse getUser(@PathVariable Long userId) {
        User user = userService.getUser(userId);
        return modelMapper.map(user, UserResponse.class);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public List<UserResponse> getUsers(@RequestParam(value = "page", defaultValue = "0") int page,
                                        @RequestParam(value = "limit", defaultValue = "25") int limit) {
        if (page > 0) page--;
        List<User> userEntities = userService.getUsers(page, limit);
        List<UserResponse> userResponses = new ArrayList<>();

        userEntities.forEach(u -> userResponses.add(modelMapper.map(u, UserResponse.class)));
        return userResponses;
    }
}

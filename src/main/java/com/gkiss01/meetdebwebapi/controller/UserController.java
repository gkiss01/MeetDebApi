package com.gkiss01.meetdebwebapi.controller;

import com.gkiss01.meetdebwebapi.entity.User;
import com.gkiss01.meetdebwebapi.model.GenericResponse;
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

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE },
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public UserResponse createUser(@Valid @RequestBody UserRequest userRequest) {
        User user = userService.createUser(userRequest);
        return modelMapper.map(user, UserResponse.class);
    }

    @PreAuthorize("hasRole('CLIENT')")
    @PutMapping(consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE },
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public UserResponse updateUser(@RequestBody UserRequest userRequest, Authentication authentication) {
        UserWithId userDetails = (UserWithId) authentication.getPrincipal();

        User user = userService.updateUser(userDetails.getUserId(), userRequest);
        return modelMapper.map(user, UserResponse.class);
    }

//    @PreAuthorize("hasRole('CLIENT')")
//    @PutMapping(consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE },
//            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
//    public GenericResponse updateUser(@Valid @RequestBody UserRequest userRequest, Authentication authentication) {
//        UserWithId userDetails = (UserWithId) authentication.getPrincipal();
//
//        User user = userService.updateUser(userDetails.getUserId(), userRequest);
//        return GenericResponse.builder().error(false).user(modelMapper.map(user, UserResponse.class)).build();
//    }

//    @PreAuthorize("hasRole('CLIENT')")
//    @PutMapping(path = "/{userId}", consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE },
//            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
//    public GenericResponse updateUser(@PathVariable(required = false) Long userId, @Valid @RequestBody UserRequest userRequest, Authentication authentication) {
//        UserWithId userDetails = (UserWithId) authentication.getPrincipal();
//
//        if (userId != null && !userDetails.getAuthorities().contains(ROLE_ADMIN) && !userDetails.getUserId().equals(userId))
//            throw new CustomRuntimeException(ErrorCodes.ACCESS_DENIED);
//
//        User user = userService.updateUser(userId != null ? userId : userDetails.getUserId(), userRequest);
//        return GenericResponse.builder().error(false).user(modelMapper.map(user, UserResponse.class)).build();
//    }

    @PreAuthorize("hasRole('CLIENT')")
    @DeleteMapping(produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public SuccessResponse<Long> deleteUser(Authentication authentication) {
        UserWithId userDetails = (UserWithId) authentication.getPrincipal();

        userService.deleteUser(userDetails.getUserId());
        return new SuccessResponse<>(userDetails.getUserId());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(path = "/{userId}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public SuccessResponse<Long> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return new SuccessResponse<>(userId);
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

    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping(path = "/check", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public UserResponse checkUser(Authentication authentication) {
        UserWithId userDetails = (UserWithId) authentication.getPrincipal();

        User user = userService.getUser(userDetails.getUserId());
        return modelMapper.map(user, UserResponse.class);
    }

    @GetMapping(path = "/confirm-account", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public GenericResponse confirmUser(@RequestParam("token") String token) {
        userService.confirmUser(token);
        return GenericResponse.builder().error(false).message("User verified!").build();
    }
}

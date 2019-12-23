package com.gkiss01.meetdebwebapi.controller;

import com.gkiss01.meetdebwebapi.entity.User;
import com.gkiss01.meetdebwebapi.model.UserRequest;
import com.gkiss01.meetdebwebapi.model.UserResponse;
import com.gkiss01.meetdebwebapi.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping(consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE },
                 produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public UserResponse createUser(@Valid @RequestBody UserRequest userRequest) {
        if (userRepository.findUserByEmail(userRequest.getEmail()) != null)
            throw new RuntimeException("Record already exists!");

        ModelMapper modelMapper = new ModelMapper();
        User user = modelMapper.map(userRequest, User.class);
        //user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        user = userRepository.save(user);
        return modelMapper.map(user, UserResponse.class);
    }

    @PutMapping(path = "/{userId}", consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE },
                                    produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public UserResponse updateUser(@PathVariable Long userId, @Valid @RequestBody UserRequest userRequest) {
        User user = userRepository.findUserById(userId);

        if (user == null)
            throw new RuntimeException("User not found!");

        user.setEmail(userRequest.getEmail());
        user.setPassword(userRequest.getPassword());
        //user.setPassword(bCryptPasswordEncoder.encode(userRequest.getPassword()));
        user.setName(userRequest.getName());
        user.setRoles(userRequest.getRoles());

        user = userRepository.save(user);
        return new ModelMapper().map(user, UserResponse.class);
    }

    @DeleteMapping(path = "/{userId}", produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
    public String deleteUser(@PathVariable Long userId) {
        User user = userRepository.findUserById(userId);

        if (user == null)
            throw new RuntimeException("User not found!");

        userRepository.delete(user);
        return "User deleted!";
    }

    @GetMapping(path = "/{userId}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public UserResponse getUser(@PathVariable Long userId) {
        User user = userRepository.findUserById(userId);

        if (user == null)
            throw new RuntimeException("User not found!");

        return new ModelMapper().map(user, UserResponse.class);
    }

    @GetMapping(produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public List<UserResponse> getUsers(@RequestParam(value = "page", defaultValue = "0") int page,
                                       @RequestParam(value = "limit", defaultValue = "25") int limit) {
        if (page > 0) page--;
        Pageable pageableRequest = PageRequest.of(page, limit);

        Page<User> usersPage = userRepository.findAll(pageableRequest);
        List<User> userEntities = usersPage.toList();

        if (userEntities.isEmpty())
            throw new RuntimeException("No records found!");

        ModelMapper modelMapper = new ModelMapper();
        List<UserResponse> userResponses = new ArrayList<>();

        userEntities.forEach(u -> {
            UserResponse userResponse = modelMapper.map(u, UserResponse.class);
            userResponses.add(userResponse);
        });
        return userResponses;
    }
}

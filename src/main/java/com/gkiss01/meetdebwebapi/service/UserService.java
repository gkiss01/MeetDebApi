package com.gkiss01.meetdebwebapi.service;

import com.gkiss01.meetdebwebapi.entity.User;
import com.gkiss01.meetdebwebapi.model.UserRequest;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    User createUser(UserRequest userRequest);
    User updateUser(Long userId, UserRequest userRequest);
    void deleteUser(Long userId);
    User getUser(Long userId);
    List<User> getUsers(int page, int limit);
}

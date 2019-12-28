package com.gkiss01.meetdebwebapi.service.impl;

import com.gkiss01.meetdebwebapi.entity.User;
import com.gkiss01.meetdebwebapi.model.UserRequest;
import com.gkiss01.meetdebwebapi.repository.UserRepository;
import com.gkiss01.meetdebwebapi.service.UserService;
import com.gkiss01.meetdebwebapi.service.UserWithId;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static com.gkiss01.meetdebwebapi.entity.Role.ROLE_CLIENT;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public User createUser(UserRequest userRequest) {
        if (userRepository.findUserByEmail(userRequest.getEmail()) != null)
            throw new RuntimeException("Email is already in use!");

        User user = modelMapper.map(userRequest, User.class);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setRoles(new HashSet<>(Collections.singletonList(ROLE_CLIENT)));

        user = userRepository.save(user);
        return user;
    }

    @Override
    public User updateUser(Long userId, UserRequest userRequest) {
        User user = userRepository.findUserById(userId);

        if (user == null)
            throw new RuntimeException("User not found!");

        User tempUser;
        if ((tempUser = userRepository.findUserByEmail(userRequest.getEmail())) != null && !tempUser.getId().equals(userId))
            throw new RuntimeException("Email is already in use!");

        user.setEmail(userRequest.getEmail());
        user.setPassword(bCryptPasswordEncoder.encode(userRequest.getPassword()));
        user.setName(userRequest.getName());
        user.setRoles(userRequest.getRoles());

        user = userRepository.save(user);
        return user;
    }

    @Override
    public void deleteUser(Long userId) {
        User user = userRepository.findUserById(userId);

        if (user == null)
            throw new RuntimeException("User not found!");

        userRepository.delete(user);
    }

    @Override
    public User getUser(Long userId) {
        User user = userRepository.findUserById(userId);

        if (user == null)
            throw new RuntimeException("User not found!");

        return user;
    }

    @Override
    public List<User> getUsers(int page, int limit) {
        Pageable pageableRequest = PageRequest.of(page, limit);

        Page<User> usersPage = userRepository.findAll(pageableRequest);
        List<User> userEntities = usersPage.toList();

        if (userEntities.isEmpty())
            throw new RuntimeException("No records found!");

        return userEntities;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findUserByEmail(email);

        if (user == null)
            throw new UsernameNotFoundException(email);

        return new UserWithId(user.getId(), user.getEmail(), user.getPassword(), user.getRoles());
    }
}

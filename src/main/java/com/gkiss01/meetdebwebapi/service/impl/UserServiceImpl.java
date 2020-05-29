package com.gkiss01.meetdebwebapi.service.impl;

import com.gkiss01.meetdebwebapi.entity.ConfirmationToken;
import com.gkiss01.meetdebwebapi.entity.User;
import com.gkiss01.meetdebwebapi.model.UserRequest;
import com.gkiss01.meetdebwebapi.repository.*;
import com.gkiss01.meetdebwebapi.service.UserService;
import com.gkiss01.meetdebwebapi.utils.CustomRuntimeException;
import com.gkiss01.meetdebwebapi.utils.ErrorCodes;
import com.gkiss01.meetdebwebapi.utils.UserWithId;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static com.gkiss01.meetdebwebapi.entity.Role.ROLE_ADMIN;
import static com.gkiss01.meetdebwebapi.entity.Role.ROLE_CLIENT;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final EventRepository eventRepository;
    private final ParticipantRepository participantRepository;
    private final DateRepository dateRepository;
    private final VoteRepository voteRepository;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final EmailServiceImpl emailService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, ConfirmationTokenRepository confirmationTokenRepository, EventRepository eventRepository, ParticipantRepository participantRepository, DateRepository dateRepository, VoteRepository voteRepository, ModelMapper modelMapper, BCryptPasswordEncoder bCryptPasswordEncoder, EmailServiceImpl emailService) {
        this.userRepository = userRepository;
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.eventRepository = eventRepository;
        this.participantRepository = participantRepository;
        this.dateRepository = dateRepository;
        this.voteRepository = voteRepository;
        this.modelMapper = modelMapper;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.emailService = emailService;
    }

    @Override
    public User createUser(UserRequest userRequest) {
        if (userRepository.findUserByEmail(userRequest.getEmail()) != null)
            throw new CustomRuntimeException(ErrorCodes.EMAIL_ALREADY_IN_USE);

        User user = modelMapper.map(userRequest, User.class);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setRoles(new HashSet<>(Collections.singletonList(ROLE_CLIENT)));
        user.setEnabled(false);

        user = userRepository.save(user);

        ConfirmationToken confirmationToken = new ConfirmationToken(user);
        confirmationTokenRepository.save(confirmationToken);

        emailService.sendConfirmationMessage(user.getEmail(), "http://192.168.1.106:8080/users/confirm?token=" + confirmationToken.getToken());
        return user;
    }

    @Override
    public User updateUser(Long userId, UserRequest userRequest, UserWithId userDetails) {
        User user = userRepository.findUserById(userId);

        if (user == null)
            throw new CustomRuntimeException(ErrorCodes.USER_NOT_FOUND);

        User tempUser;
        if ((tempUser = userRepository.findUserByEmail(userRequest.getEmail())) != null && !tempUser.getId().equals(userId))
            throw new CustomRuntimeException(ErrorCodes.EMAIL_ALREADY_IN_USE);

        if (userRequest.getEmail() != null) user.setEmail(userRequest.getEmail());
        if (userRequest.getPassword() != null) user.setPassword(bCryptPasswordEncoder.encode(userRequest.getPassword()));
        if (userRequest.getName() != null) user.setName(userRequest.getName());
        if (userDetails.getAuthorities().contains(ROLE_ADMIN) && userRequest.getRoles() != null) user.setRoles(userRequest.getRoles());

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findUserById(userId);

        if (user == null)
            throw new CustomRuntimeException(ErrorCodes.USER_NOT_FOUND);

        participantRepository.deleteById_UserId(userId);
        participantRepository.deleteParticipantsByEventCreator(userId);
        voteRepository.deleteById_UserId(userId);
        voteRepository.deleteVotesByDateEventCreator(userId);
        dateRepository.deleteDatesByEventCreator(userId);
        eventRepository.deleteByUserId(userId);
        confirmationTokenRepository.deleteByUserId(userId);
        userRepository.delete(user);
    }

    @Override
    public User getUser(Long userId) {
        User user = userRepository.findUserById(userId);

        if (user == null)
            throw new CustomRuntimeException(ErrorCodes.USER_NOT_FOUND);

        return user;
    }

    @Override
    public List<User> getUsers(int page, int limit) {
        Pageable pageableRequest = PageRequest.of(page, limit);

        Page<User> usersPage = userRepository.findAll(pageableRequest);
        return usersPage.toList();
    }

    @Override
    public User confirmUser(String token) {
        ConfirmationToken confirmationToken = confirmationTokenRepository.findConfirmationTokenByToken(token);

        if (confirmationToken == null)
            throw new CustomRuntimeException(ErrorCodes.CONFIRMATION_TOKEN_NOT_FOUND);

        if (confirmationToken.getUser().getEnabled())
            throw new CustomRuntimeException(ErrorCodes.USER_ALREADY_VERIFIED);

        User user = confirmationToken.getUser();
        user.setEnabled(true);

        return userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findUserByEmail(email);

        if (user == null)
            throw new UsernameNotFoundException(email);

        return new UserWithId(user.getId(), user.getEmail(), user.getPassword(), user.getEnabled(), true, true, true, user.getRoles());
    }
}

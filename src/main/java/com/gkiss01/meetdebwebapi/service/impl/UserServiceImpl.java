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

import static com.gkiss01.meetdebwebapi.entity.Role.ROLE_CLIENT;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private DateRepository dateRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private EmailServiceImpl emailService;

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

        emailService.sendConfirmationMessage(user.getEmail(), "http://172.17.172.157:8080/users/confirm-account?token=" + confirmationToken.getToken());
        return user;
    }

    @Override
    public User updateUser(Long userId, UserRequest userRequest) {
        User user = userRepository.findUserById(userId);

        if (user == null)
            throw new CustomRuntimeException(ErrorCodes.USER_NOT_FOUND);

        User tempUser;
        if ((tempUser = userRepository.findUserByEmail(userRequest.getEmail())) != null && !tempUser.getId().equals(userId))
            throw new CustomRuntimeException(ErrorCodes.EMAIL_ALREADY_IN_USE);

        user.setEmail(userRequest.getEmail());
        user.setPassword(bCryptPasswordEncoder.encode(userRequest.getPassword()));
        user.setName(userRequest.getName());
        if (userRequest.getRoles() != null) user.setRoles(userRequest.getRoles());

        user = userRepository.save(user);
        return user;
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
        List<User> userEntities = usersPage.toList();

        if (userEntities.isEmpty())
            throw new CustomRuntimeException(ErrorCodes.NO_USERS_FOUND);

        return userEntities;
    }

    @Override
    public void confirmUser(String token) {
        ConfirmationToken confirmationToken = confirmationTokenRepository.findConfirmationTokenByToken(token);

        if (confirmationToken == null)
            throw new CustomRuntimeException(ErrorCodes.CONFIRMATION_TOKEN_NOT_FOUND);

        if (confirmationToken.getUser().getEnabled())
            throw new CustomRuntimeException(ErrorCodes.USER_ALREADY_VERIFIED);

        User user = confirmationToken.getUser();
        user.setEnabled(true);

        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findUserByEmail(email);

        if (user == null)
            throw new UsernameNotFoundException(email);

        return new UserWithId(user.getId(), user.getEmail(), user.getPassword(), user.getEnabled(), true, true, true, user.getRoles());
    }
}

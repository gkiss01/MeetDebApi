package com.gkiss01.meetdebwebapi.repository;

import com.gkiss01.meetdebwebapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findUserByEmail(String email);

    User findUserById(Long id);
}

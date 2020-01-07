package com.gkiss01.meetdebwebapi.repository;

import com.gkiss01.meetdebwebapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {

    User findUserByEmail(String email);

    User findUserById(Long id);

    @SuppressWarnings("SpringDataRepositoryMethodReturnTypeInspection")
    @Query(value = "SELECT name FROM users WHERE id = ?1", nativeQuery = true)
    String findNameById(Long id);

    @Query(value = "SELECT * FROM users ORDER BY RAND() LIMIT 1", nativeQuery = true)
    User findUserByRandom();
}

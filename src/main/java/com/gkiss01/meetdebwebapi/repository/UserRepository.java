package com.gkiss01.meetdebwebapi.repository;

import com.gkiss01.meetdebwebapi.entity.EventSummary;
import com.gkiss01.meetdebwebapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

    User findUserByEmail(String email);

    User findUserById(Long id);

    @SuppressWarnings("SpringDataRepositoryMethodReturnTypeInspection")
    @Query(value = "SELECT u.name FROM User u WHERE u.id = :userId")
    String findNameById(@Param("userId") Long userId);

    @Query(value = "SELECT id FROM users ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Long findUserIdByRandom();

    @SuppressWarnings("SpringDataRepositoryMethodReturnTypeInspection")
    @Query("SELECT NEW com.gkiss01.meetdebwebapi.entity.EventSummary(u.id, (SELECT COUNT(e) FROM Event e WHERE e.userId = u.id) AS eventsCreated,\n" +
            "(SELECT COUNT(e) FROM Event e WHERE e.userId = u.id) AS eventsInvolved)\n" +
            "FROM User u WHERE u.id = :userId")
    EventSummary getEventsSummaryById(@Param("userId") Long userId);
}

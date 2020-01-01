package com.gkiss01.meetdebwebapi.repository;

import com.gkiss01.meetdebwebapi.entity.ConfirmationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long> {

    ConfirmationToken findConfirmationTokenByToken(String token);

    void deleteByUserId(Long userId);
}

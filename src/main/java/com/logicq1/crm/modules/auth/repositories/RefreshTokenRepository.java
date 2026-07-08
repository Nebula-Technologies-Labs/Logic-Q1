package com.logicq1.crm.modules.auth.repositories;

import com.logicq1.crm.modules.auth.entity.RefreshToken;
import com.logicq1.crm.modules.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository

public interface RefreshTokenRepository

        extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByToken(String token);

    List<RefreshToken> findByUserAndRevokedFalse(User user);

}
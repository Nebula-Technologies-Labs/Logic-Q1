package com.logicq1.crm.modules.forgotPassword.repositories;

import com.logicq1.crm.modules.forgotPassword.entity.PasswordResetTokens;
import com.logicq1.crm.modules.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetTokens, UUID> {

    @Query("select prt from PasswordResetTokens prt where prt.token = ?1 and prt.user = ?2")
    Optional<PasswordResetTokens> findByOtpAndUser(Integer token, User user);

    @Modifying
    void deleteByUser(User user);

    Optional<PasswordResetTokens> findByUser(User user);
}

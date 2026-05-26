package com.logicq1.crm.modules.forgotPassword.repositories;

import com.logicq1.crm.modules.forgotPassword.entity.password_reset_tokens;
import com.logicq1.crm.modules.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<password_reset_tokens,Integer> {

    @Query("select prt from password_reset_tokens prt where prt.token = ?1 and prt.user = ?2")
    Optional<password_reset_tokens> findByOtpAndUser(Integer token, User user);

    void deleteByUser(User user);

    Optional<password_reset_tokens> findByUser(User user);
}

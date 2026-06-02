package com.logicq1.crm.modules.user.repository;

import com.logicq1.crm.modules.user.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByOrganizationId(UUID organizationId);

    Page<User> findByOrganizationId(UUID organizationId, Pageable pageable);

    List<User> findByOrganizationIdAndIsActiveTrue(UUID organizationId);

    Optional<User> findByIdAndOrganizationId(UUID userId, UUID organizationId);

    @Modifying
    @Transactional
    @Query("update User u set u.password = ?2 where u.email = ?1")
    void updatePassword(String email, String password);

    @Modifying
    @Transactional
    @Query("update User u set u.isActive = false where u.id = ?1")
    void deactivateUser(UUID userId);

    @Modifying
    @Transactional
    @Query("update User u set u.isActive = true where u.id = ?1")
    void activateUser(UUID userId);

    @Modifying
    @Transactional
    @Query("update User u set u.lastLoginAt = ?2 where u.email = ?1")
    void updateLastLogin(String email, LocalDateTime lastLoginAt);


}
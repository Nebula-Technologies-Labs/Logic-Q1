package com.logicq1.crm.modules.organization.repository;

import com.logicq1.crm.modules.organization.entity.Roles;
import com.logicq1.crm.modules.organization.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RolesRepository extends JpaRepository<Roles, UUID> {

    Optional<Roles> findByRole(UserRole role);
}

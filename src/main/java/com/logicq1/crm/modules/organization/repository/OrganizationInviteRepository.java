package com.logicq1.crm.modules.organization.repository;

import com.logicq1.crm.modules.organization.entity.Organization;
import com.logicq1.crm.modules.organization.entity.OrganizationInvite;
import com.logicq1.crm.modules.organization.enums.InviteStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrganizationInviteRepository
        extends JpaRepository<OrganizationInvite, UUID> {

    Optional<OrganizationInvite> findByToken(String token);

    boolean existsByEmailAndOrganizationAndStatus(
            String email,
            Organization organization,
            InviteStatus status
    );
}
package com.logicq1.crm.modules.organization.repository;

import com.logicq1.crm.modules.organization.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, String> {
}

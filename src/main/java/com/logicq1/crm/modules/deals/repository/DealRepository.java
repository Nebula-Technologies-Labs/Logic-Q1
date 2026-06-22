package com.logicq1.crm.modules.deals.repository;

import com.logicq1.crm.enums.DealStage;
import com.logicq1.crm.modules.deals.entity.Deal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DealRepository extends JpaRepository<Deal, UUID> {

    List<Deal> findByOrganizationId(UUID organizationId);

    List<Deal> findByOrganizationIdAndStage(
            UUID organizationId,
            DealStage stage
    );
    boolean existsByDealNameIgnoreCaseAndOrganizationId(
            String dealName,
            UUID organizationId
    );

    List<Deal> findByOwnerId(UUID ownerId);
}

package com.logicq1.crm.modules.lead.repository;

import com.logicq1.crm.modules.lead.entity.Lead;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface LeadRepository extends JpaRepository<Lead, UUID> {


    @Modifying
    @Transactional
    @Query("DELETE FROM Lead l WHERE l.id = :leadId")
    int deleteByIdAndReturnCount(@Param("leadId") UUID leadId);

    boolean existsByEmail(String email);
}

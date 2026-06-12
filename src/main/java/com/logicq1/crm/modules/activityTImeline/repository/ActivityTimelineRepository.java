package com.logicq1.crm.modules.activityTImeline.repository;


import com.logicq1.crm.modules.activityTImeline.entity.ActivityTimeline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ActivityTimelineRepository
        extends JpaRepository<ActivityTimeline, UUID> {

    List<ActivityTimeline> findByEntityTypeAndEntityIdOrderByCreatedAtDesc(
            String entityType,
            UUID entityId
    );
}
package com.logicq1.crm.modules.activityTImeline.service;

import com.logicq1.crm.enums.ActivityVisibility;
import com.logicq1.crm.enums.AuditAction;
import com.logicq1.crm.modules.activityTImeline.entity.ActivityTimeline;
import com.logicq1.crm.modules.activityTImeline.repository.ActivityTimelineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ActivityTimelineService {

    private final ActivityTimelineRepository repository;


    public List<ActivityTimeline> getTimeline(
            String entityType,
            UUID entityId
    ) {
        return repository.findByEntityTypeAndEntityIdOrderByCreatedAtDesc(
                entityType,
                entityId
        );
    }

    public List<ActivityTimeline> getOrganizationFeed(
            UUID organizationId
    ) {
        System.out.println(organizationId);
        return repository
                .findByOrganizationIdAndVisibilityOrderByCreatedAtDesc(
                        organizationId,
                        ActivityVisibility.ORGANIZATION_FEED
                );
    }
}
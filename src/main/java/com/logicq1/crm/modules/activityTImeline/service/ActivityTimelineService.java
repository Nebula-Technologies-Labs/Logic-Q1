package com.logicq1.crm.modules.activityTImeline.service;

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

    public void logCreate(
            String entityType,
            UUID entityId,
            UUID performedBy
    ) {

        ActivityTimeline activity = ActivityTimeline.builder()
                .entityType(entityType)
                .entityId(entityId)
                .actionType("CREATE")
                .performedBy(performedBy)
                .build();

        repository.save(activity);
    }

    public void logUpdate(
            String entityType,
            UUID entityId,
            String fieldName,
            String oldValue,
            String newValue,
            UUID performedBy
    ) {

        ActivityTimeline activity = ActivityTimeline.builder()
                .entityType(entityType)
                .entityId(entityId)
                .actionType("UPDATE")
                .fieldName(fieldName)
                .oldValue(oldValue)
                .newValue(newValue)
                .performedBy(performedBy)
                .build();

        repository.save(activity);
    }

    public void logDelete(
            String entityType,
            UUID entityId,
            UUID performedBy
    ) {

        ActivityTimeline activity = ActivityTimeline.builder()
                .entityType(entityType)
                .entityId(entityId)
                .actionType("DELETE")
                .performedBy(performedBy)
                .build();

        repository.save(activity);
    }

    public List<ActivityTimeline> getTimeline(
            String entityType,
            UUID entityId
    ) {
        return repository.findByEntityTypeAndEntityIdOrderByCreatedAtDesc(
                entityType,
                entityId
        );
    }
}
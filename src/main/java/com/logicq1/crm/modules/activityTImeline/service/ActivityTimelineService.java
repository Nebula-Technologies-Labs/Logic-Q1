package com.logicq1.crm.modules.activityTImeline.service;

import com.logicq1.crm.enums.ActivityVisibility;
import com.logicq1.crm.enums.AuditAction;
import com.logicq1.crm.modules.activityTImeline.entity.ActivityTimeline;
import com.logicq1.crm.modules.activityTImeline.repository.ActivityTimelineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.sql.SQLOutput;
import java.util.List;
import java.util.Objects;
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
                .actionType(AuditAction.CREATE)
                .performedBy(performedBy)
                .visibility(ActivityVisibility.ENTITY_ONLY)
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
                .actionType(AuditAction.UPDATE)
                .fieldName(fieldName)
                .oldValue(oldValue)
                .newValue(newValue)
                .performedBy(performedBy)
                .visibility(ActivityVisibility.ENTITY_ONLY)
                .build();

        repository.save(activity);
    }
    public void logChanges(
            String entityType,
            UUID entityId,
            Object oldObj,
            Object newObj,
            UUID userId
    ) {

        for (Field field : oldObj.getClass().getDeclaredFields()) {

            try {

                field.setAccessible(true);

                Object oldValue = field.get(oldObj);
                Object newValue = field.get(newObj);

                if (!Objects.equals(oldValue, newValue)) {

                    logUpdate(
                            entityType,
                            entityId,
                            field.getName(),
                            oldValue == null ? null : oldValue.toString(),
                            newValue == null ? null : newValue.toString(),
                            userId
                    );
                }

            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void logDelete(
            String entityType,
            UUID entityId,
            UUID performedBy
    ) {

        ActivityTimeline activity = ActivityTimeline.builder()
                .entityType(entityType)
                .entityId(entityId)
                .actionType(AuditAction.DELETE)
                .performedBy(performedBy)
                .visibility(ActivityVisibility.ENTITY_ONLY)
                .build();

        repository.save(activity);
    }
    public void logConvert(
            String sourceEntityType,
            UUID sourceEntityId,
            String targetEntityType,
            UUID targetEntityId,
            UUID organizationId,
            UUID performedBy
    ) {

        ActivityTimeline activity = ActivityTimeline.builder()
                .entityType(sourceEntityType)
                .entityId(sourceEntityId)
                .actionType(AuditAction.CONVERT)
                .oldValue(sourceEntityType)
                .newValue(targetEntityType)
                .organizationId(organizationId)
                .performedBy(performedBy)
                .visibility(ActivityVisibility.ORGANIZATION_FEED)
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
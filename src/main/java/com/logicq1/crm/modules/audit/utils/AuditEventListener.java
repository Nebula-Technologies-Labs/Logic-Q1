package com.logicq1.crm.modules.audit.utils;


import com.logicq1.crm.enums.ActivityVisibility;
import com.logicq1.crm.modules.audit.entity.AuditIgnore;
import com.logicq1.crm.modules.audit.entity.AuditableEntity;
import com.logicq1.crm.modules.audit.entity.OrganizationFeed;
import com.logicq1.crm.modules.audit.events.EntityCreatedEvent;
import com.logicq1.crm.modules.audit.events.EntityDeletedEvent;
import com.logicq1.crm.modules.audit.events.EntityUpdatedEvent;
import com.logicq1.crm.modules.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.event.spi.*;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuditEventListener implements
        PostInsertEventListener,
        PostUpdateEventListener,
        PostDeleteEventListener {

    private final ApplicationEventPublisher publisher;

    @Override
    public void onPostInsert(PostInsertEvent event) {

        System.out.println("POST INSERT FIRED");

        if (!isAuditable(event.getEntity())) {
            System.out.println("NOT AUDITABLE");
            return;
        }

        System.out.println("PUBLISHING EVENT");

        publisher.publishEvent(
                new EntityCreatedEvent(
                        event.getEntity()
                                .getClass()
                                .getSimpleName(),
                        extractId(event),
                        extractOrganizationId(),
                        extractPerformedBy(),
                        ActivityVisibility.ENTITY_FEED
                )
        );
    }

    @Override
    public void onPostDelete(PostDeleteEvent event) {

        if (!isAuditable(event.getEntity())) {
            return;
        }

        publisher.publishEvent(
                new EntityDeletedEvent(
                        event.getEntity()
                                .getClass()
                                .getSimpleName(),
                        extractId(event),
                        extractOrganizationId(),
                        extractPerformedBy(),
                        ActivityVisibility.ENTITY_FEED
                )
        );
    }

    @Override
    public void onPostUpdate(PostUpdateEvent event) {

        if (!isAuditable(event.getEntity())) {
            return;
        }

        String[] propertyNames =
                event.getPersister().getPropertyNames();

        Object[] oldState = event.getOldState();
        Object[] newState = event.getState();

        for (int i = 0; i < propertyNames.length; i++) {

            if (isIgnoredField(
                    event.getEntity(),
                    propertyNames[i]
            )) {
                continue;
            }

            if (java.util.Objects.equals(
                    oldState[i],
                    newState[i]
            )) {
                continue;
            }

            ActivityVisibility visibility =
                    isOrganizationFeedField(
                            event.getEntity(),
                            propertyNames[i]
                    )
                            ? ActivityVisibility.ORGANIZATION_FEED
                            : ActivityVisibility.ENTITY_FEED;

            publisher.publishEvent(
                    new EntityUpdatedEvent(
                            event.getEntity()
                                    .getClass()
                                    .getSimpleName(),
                            extractId(event),
                            extractOrganizationId(),
                            extractPerformedBy(),
                            propertyNames[i],
                            String.valueOf(oldState[i]),
                            String.valueOf(newState[i]),
                            visibility
                    )
            );
        }
    }

    private boolean isAuditable(Object entity) {
        return entity.getClass()
                .isAnnotationPresent(AuditableEntity.class);
    }

    private UUID extractId(
            AbstractEvent event
    ) {

        Object entity;

        if (event instanceof PostInsertEvent e) {
            entity = e.getEntity();
        } else if (event instanceof PostUpdateEvent e) {
            entity = e.getEntity();
        } else if (event instanceof PostDeleteEvent e) {
            entity = e.getEntity();
        } else {
            throw new IllegalArgumentException();
        }

        try {

            Method method =
                    entity.getClass()
                            .getMethod("getId");

            return (UUID) method.invoke(entity);

        } catch (Exception ex) {

            throw new RuntimeException(
                    "Unable to extract entity id",
                    ex
            );
        }
    }

    @Override
    public boolean requiresPostCommitHandling(
            EntityPersister persister
    ) {
        return false;
    }

    private boolean isIgnoredField(
            Object entity,
            String fieldName
    ) {
        try {
            Field field =
                    entity.getClass()
                            .getDeclaredField(fieldName);

            return field.isAnnotationPresent(
                    AuditIgnore.class
            );

        } catch (NoSuchFieldException e) {
            return false;
        }
    }

    private boolean isOrganizationFeedField(
            Object entity,
            String fieldName
    ) {

        try {

            Field field =
                    entity.getClass()
                            .getDeclaredField(
                                    fieldName
                            );

            return field.isAnnotationPresent(
                    OrganizationFeed.class
            );

        } catch (NoSuchFieldException e) {

            return false;
        }
    }
    private UUID extractPerformedBy() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null) {
            return null;
        }

        Object principal =
                authentication.getPrincipal();

        if (principal instanceof User user) {
            return user.getId();
        }

        return null;
    }
    private UUID extractOrganizationId() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null) {
            return null;
        }

        Object principal =
                authentication.getPrincipal();

        if (principal instanceof User user) {
            return user.getOrganization()
                    .getId();
        }

        return null;
    }



}
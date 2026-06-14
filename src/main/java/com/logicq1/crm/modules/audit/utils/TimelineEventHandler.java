package com.logicq1.crm.modules.audit.utils;

import com.logicq1.crm.enums.AuditAction;
import com.logicq1.crm.modules.activityTImeline.entity.ActivityTimeline;
import com.logicq1.crm.modules.activityTImeline.repository.ActivityTimelineRepository;
import com.logicq1.crm.modules.audit.events.EntityCreatedEvent;
import com.logicq1.crm.modules.audit.events.EntityDeletedEvent;
import com.logicq1.crm.modules.audit.events.EntityUpdatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class TimelineEventHandler {

    private final ActivityTimelineRepository timelineRepository;

    @Transactional(
            propagation = Propagation.REQUIRES_NEW
    )
    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handle(
            EntityCreatedEvent event
    ) {

        ActivityTimeline timeline =
                new ActivityTimeline();

        timeline.setActionType(
                AuditAction.CREATE
        );

        timeline.setEntityType(
                event.entityType()
        );

        timeline.setEntityId(
                event.entityId()
        );

        timeline.setOrganizationId(
                event.organizationId()
        );

        timeline.setPerformedBy(
                event.performedBy()
        );

        timeline.setVisibility(
                event.visibility()
        );

        timelineRepository.save(timeline);
    }

    @Transactional(
            propagation = Propagation.REQUIRES_NEW
    )
    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handle(
            EntityDeletedEvent event
    ) {

        ActivityTimeline timeline =
                new ActivityTimeline();

        timeline.setActionType(
                AuditAction.DELETE
        );

        timeline.setEntityType(
                event.entityType()
        );

        timeline.setEntityId(
                event.entityId()
        );
        timeline.setOrganizationId(
                event.organizationId()
        );

        timeline.setPerformedBy(
                event.performedBy()
        );

        timeline.setVisibility(
                event.visibility()
        );

        timelineRepository.save(timeline);
    }

    @Transactional(
            propagation = Propagation.REQUIRES_NEW
    )
    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handle(
            EntityUpdatedEvent event
    ) {

        ActivityTimeline timeline =
                new ActivityTimeline();

        timeline.setActionType(
                AuditAction.UPDATE
        );

        timeline.setEntityType(
                event.entityType()
        );

        timeline.setEntityId(
                event.entityId()
        );

        timeline.setFieldName(
                event.fieldName()
        );

        timeline.setOldValue(
                event.oldValue()
        );

        timeline.setNewValue(
                event.newValue()
        );
        timeline.setOrganizationId(
                event.organizationId()
        );

        timeline.setPerformedBy(
                event.performedBy()
        );

        timeline.setVisibility(
                event.visibility()
        );

        timelineRepository.save(timeline);
    }
}
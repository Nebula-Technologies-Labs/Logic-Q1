package com.logicq1.crm.modules.activityTImeline.entity;

import com.logicq1.crm.enums.ActivityVisibility;
import com.logicq1.crm.enums.AuditAction;
import com.logicq1.crm.modules.activityTImeline.dto.FieldChange;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "activity_timeline")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityTimeline {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String entityType;

    @Column(nullable = false)
    private UUID entityId;

    @Enumerated(EnumType.STRING)
    private AuditAction actionType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<FieldChange> changes;

    private UUID performedBy;

    private UUID organizationId;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private ActivityVisibility visibility;


    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }


}
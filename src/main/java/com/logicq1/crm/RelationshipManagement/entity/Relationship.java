package com.logicq1.crm.RelationshipManagement.entity;

import com.logicq1.crm.MetadataEngine.FieldManagement.entity.FieldDefinition;
import com.logicq1.crm.MetadataEngine.ModuleManagement.entity.Module;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "relationships")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Relationship {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "source_module_id", nullable = false)
    private Module sourceModule;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "target_module_id", nullable = false)
    private Module targetModule;

    @Column(name = "relationship_name", nullable = false)
    private String relationshipName;

    @Column(name = "relationship_type", nullable = false)
    private String relationshipType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_field_id")
    private FieldDefinition sourceField;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_field_id")
    private FieldDefinition targetField;

    @Column(columnDefinition = "jsonb")
    private String metadata;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
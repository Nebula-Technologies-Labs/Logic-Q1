package com.logicq1.crm.MetadataEngine.FieldManagement.entity;

import com.logicq1.crm.MetadataEngine.FieldManagement.enums.FieldTypes;
import com.logicq1.crm.MetadataEngine.ModuleManagement.entity.Module;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "field_definitions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;

    @Column(nullable = false)
    private String label;

    @Column(name = "api_name", nullable = false)
    private String apiName;

    @Enumerated(EnumType.STRING)
    @Column(name = "field_type", nullable = false)
    private FieldTypes fieldType;

    @Column(columnDefinition = "text")
    private String description;

    private Boolean required;

    @Column(name = "unique_field")
    private Boolean uniqueField;

    private Boolean searchable;
    private Boolean filterable;
    private Boolean sortable;

    @Column(name = "default_value", columnDefinition = "text")
    private String defaultValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lookup_module_id")
    private Module lookupModule;

    @Column(name = "formula_expression", columnDefinition = "text")
    private String formulaExpression;

    @Column(columnDefinition = "jsonb")
    private String metadata;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
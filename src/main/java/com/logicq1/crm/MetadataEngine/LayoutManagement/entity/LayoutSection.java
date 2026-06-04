package com.logicq1.crm.MetadataEngine.LayoutManagement.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "layout_sections")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LayoutSection {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "layout_id", nullable = false)
    private Layout layout;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "text")
    private String description;

    private Integer position;

    private Boolean collapsible;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
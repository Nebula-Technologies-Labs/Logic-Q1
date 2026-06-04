package com.logicq1.crm.MetadataEngine.LayoutManagement.entity;

import com.logicq1.crm.MetadataEngine.ModuleManagement.entity.Module;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "layouts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Layout {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "is_default")
    private Boolean isDefault;

    @Column(name = "current_version")
    private Integer currentVersion;

    @Column(name = "layout_snapshot", columnDefinition = "jsonb")
    private String layoutSnapshot;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
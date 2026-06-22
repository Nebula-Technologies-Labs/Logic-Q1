package com.logicq1.crm.modules.deals.entity;

import com.logicq1.crm.enums.DealStage;
import com.logicq1.crm.enums.DealType;
import com.logicq1.crm.enums.LeadSource;
import com.logicq1.crm.modules.audit.entity.AuditIgnore;
import com.logicq1.crm.modules.audit.entity.AuditableEntity;
import com.logicq1.crm.modules.audit.entity.OrganizationFeed;
import com.logicq1.crm.modules.contacts.entity.Contact;
import com.logicq1.crm.modules.organization.entity.Organization;
import com.logicq1.crm.modules.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "deals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@AuditableEntity
public class Deal {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String dealName;

    @Column(precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(precision = 15, scale = 2)
    private BigDecimal expectedRevenue;

    @OrganizationFeed
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DealStage stage = DealStage.QUALIFICATION;

    @Enumerated(EnumType.STRING)
    private DealType type;

    @Min(0)
    @Max(100)
    private Integer probability;

    private String nextStep;

    @Enumerated(EnumType.STRING)
    private LeadSource leadSource;

    private String campaignSource;

    private LocalDate closingDate;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id")
    private Contact contact;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @AuditIgnore
    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }
}

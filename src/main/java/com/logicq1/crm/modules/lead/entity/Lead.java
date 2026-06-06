package com.logicq1.crm.modules.lead.entity;

import com.logicq1.crm.enums.Industry;
import com.logicq1.crm.enums.LeadRating;
import com.logicq1.crm.enums.LeadSource;
import com.logicq1.crm.enums.LeadStatus;
import com.logicq1.crm.modules.organization.entity.Organization;
import com.logicq1.crm.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "lead")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lead {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    // Basic Information
    private String title;
    private String firstName;
    private String lastName;
    private String company;

    // Contact Information
    private String email;
    private String secondaryEmail;
    private String phone;
    private String mobile;
    private String fax;
    private String website;
    private String twitterHandle;

    // Business Information
    private BigDecimal annualRevenue;
    private Integer employeeCount;

    private Boolean emailOptOut;

    // Address
    private String street;
    private String city;
    private String state;
    private String postalCode;
    private String country;

    // Description
    @Column(columnDefinition = "TEXT")
    private String description;

    // Lookup Fields
    @Enumerated(EnumType.STRING)
    private Industry industry;

    @Enumerated(EnumType.STRING)
    private LeadStatus leadStatus;

    @Enumerated(EnumType.STRING)
    private LeadSource leadSource;

    @Enumerated(EnumType.STRING)
    private LeadRating rating;

    // Conversion Information
    private Boolean converted;
    private LocalDateTime convertedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "converted_by")
    private User convertedBy;

    private UUID convertedContactId;

    // Audit
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private User updatedBy;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}

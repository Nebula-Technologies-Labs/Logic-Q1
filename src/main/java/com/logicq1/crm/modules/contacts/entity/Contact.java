package com.logicq1.crm.modules.contacts.entity;

import com.logicq1.crm.modules.organization.entity.Organization;
import com.logicq1.crm.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    private String email;

    private String secondaryEmail;

    private String phoneNumber;

    private String description;

    private String note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id" , nullable = false)
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id" , nullable = false)
    private Organization organization;

    private String department;

    private String contactSource;

    private String title;

    private String Address;

    private String country;

    private String state;

    private String city;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}

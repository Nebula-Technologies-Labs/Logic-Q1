package com.logicq1.crm.modules.organization.entity;

import com.logicq1.crm.modules.contacts.entity.Contact;
import com.logicq1.crm.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.security.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "organization")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    private String description;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "admin_id" , referencedColumnName = "id")
    private User adminId;

    @OneToMany(mappedBy = "organization" , cascade = CascadeType.ALL)
    private List<User> members;

    @OneToMany(mappedBy = "organization" , cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Contact> contacts;

    private String mobileNumber;

    private String websiteUrl;

    private String Category;

    private String country;

    private String state;

    private String city;

    private String pinCode;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}

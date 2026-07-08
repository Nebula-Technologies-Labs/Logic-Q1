package com.logicq1.crm.modules.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.logicq1.crm.enums.AuthProvider;
import com.logicq1.crm.modules.contacts.entity.Contact;
import com.logicq1.crm.modules.organization.entity.Organization;
import com.logicq1.crm.modules.organization.entity.Roles;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "email", name = "uk_users_email")
        },
        indexes = {
                @Index(name = "idx_users_organization_id", columnList = "organization_id"),
                @Index(name = "idx_users_is_active", columnList = "is_active"),
                @Index(name = "idx_users_email", columnList = "email")
        }
)
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true)
    private String email;

    private String password;

    private String phoneNumber;

    private String profileImage;

    private String jobTitle;

    private String department;

    @Column(nullable = false)
    private Boolean onBoarded;

    @Column(nullable = false)
    private Boolean isActive;

    @Column(nullable = false)
    private Boolean emailVerified;

    @Enumerated(EnumType.STRING)
    private AuthProvider authBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    @JsonIgnore
    private User createdBy;

    private LocalDateTime lastLoginAt;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Contact> contacts;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Roles role;

    @Column(nullable = false)
    private boolean isTermsAccepted;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return role.getRolePermissions()
                .stream()
                .map(rolePermission ->
                        new SimpleGrantedAuthority(
                                rolePermission
                                        .getPermission()
                                        .getName()
                        )
                )
                .toList();
    }
    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


}
package com.logicq1.crm.modules.auth.entity;

import com.logicq1.crm.modules.organization.entity.Roles;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "permissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permissions {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    private String description;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Roles role;

    @OneToMany(mappedBy = "permission")
    private List<RolePermission> rolePermissions;
}

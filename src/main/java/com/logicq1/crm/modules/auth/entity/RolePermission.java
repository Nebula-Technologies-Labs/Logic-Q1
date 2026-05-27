package com.logicq1.crm.modules.auth.entity;


import com.logicq1.crm.modules.organization.entity.Roles;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "permissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RolePermission {
    @Id
    @ManyToOne
    private Roles role;

    @OneToMany
    private List<Permissions> permissions;

}

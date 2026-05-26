package com.logicq1.crm.modules.auth.entity;

import com.logicq1.crm.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class password_reset_tokens {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    private User user;

    private Integer token;

    @Column(nullable = false)
    private Date expire_at;

    @Column(nullable = false)
    private Instant created_at;

    @Setter
    private boolean verified = false;


}

package com.logicq1.crm.modules.organization.entity;

import com.logicq1.crm.modules.contacts.entity.Contact;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

;

@Entity
@Table(name = "entity_tags")
public class EntityTags {
    @Id
    @ManyToOne
    @JoinColumn(name = "contact_id")
    private Contact contact;

    @Id
    @ManyToOne
    @JoinColumn(name = "tag_id")
    private Tags tag;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
package com.logicq1.crm.modules.task.entity;

import com.logicq1.crm.enums.TaskPriority;
import com.logicq1.crm.enums.TaskStatus;
import com.logicq1.crm.modules.audit.entity.AuditIgnore;
import com.logicq1.crm.modules.audit.entity.AuditableEntity;
import com.logicq1.crm.modules.audit.entity.OrganizationFeed;
import com.logicq1.crm.modules.contacts.entity.Contact;
import com.logicq1.crm.modules.lead.entity.Lead;
import com.logicq1.crm.modules.organization.entity.Organization;
import com.logicq1.crm.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * A task / to-do item. Modelled on the Zoho CRM Tasks module and surfaced to
 * the client as a simple kanban board where {@link TaskStatus} forms the columns
 * and {@code boardPosition} orders the cards inside each column.
 */
@Entity
@Table(
        name = "task",
        indexes = {
                @Index(name = "idx_task_organization_id", columnList = "organization_id"),
                @Index(name = "idx_task_owner_id", columnList = "owner_id"),
                @Index(name = "idx_task_status", columnList = "status")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@AuditableEntity
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /** Name of the task. Mandatory, like Zoho's "Subject". */
    @OrganizationFeed
    @Column(nullable = false)
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String description;

    @OrganizationFeed
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

    @Enumerated(EnumType.STRING)
    private TaskPriority priority;

    /** Due date for the task. */
    private LocalDate dueDate;

    /** When the owner should be reminded ("Remind At"). */
    private LocalDateTime reminderAt;

    /** Send a notification email to the task owner. */
    private Boolean sendNotificationEmail;

    /** Mark the task as a recurring activity. */
    private Boolean recurringActivity;

    /** Ordering of the card within its status column on the kanban board. */
    @Column(nullable = false)
    private Integer boardPosition;

    /** Task owner / assignee. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    /** Optional contact the task is related to ("Contacts" lookup in Zoho). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_contact_id")
    private Contact relatedContact;

    /** Optional lead the task is related to ("Accounts/related record" in Zoho). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_lead_id")
    private Lead relatedLead;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private User updatedBy;

    @CreationTimestamp
    @Column(updatable = false)
    @AuditIgnore
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @AuditIgnore
    private LocalDateTime updatedAt;
}

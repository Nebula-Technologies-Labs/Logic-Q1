package com.logicq1.crm.modules.task.mapper;

import com.logicq1.crm.modules.contacts.entity.Contact;
import com.logicq1.crm.modules.lead.entity.Lead;
import com.logicq1.crm.modules.task.dto.CreateTaskRequest;
import com.logicq1.crm.modules.task.dto.TaskDTO;
import com.logicq1.crm.modules.task.entity.Task;
import com.logicq1.crm.modules.user.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "organization", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "relatedContact", ignore = true)
    @Mapping(target = "relatedLead", ignore = true)
    @Mapping(target = "boardPosition", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Task toEntity(CreateTaskRequest request);

    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "ownerName", expression = "java(fullName(task.getOwner()))")
    @Mapping(target = "organizationId", source = "organization.id")
    @Mapping(target = "relatedContactId", source = "relatedContact.id")
    @Mapping(target = "relatedContactName", expression = "java(contactName(task.getRelatedContact()))")
    @Mapping(target = "relatedLeadId", source = "relatedLead.id")
    @Mapping(target = "relatedLeadName", expression = "java(leadName(task.getRelatedLead()))")
    @Mapping(target = "createdById", source = "createdBy.id")
    @Mapping(target = "updatedById", source = "updatedBy.id")
    TaskDTO toDTO(Task task);

    default String fullName(User user) {
        if (user == null) {
            return null;
        }
        return user.getFirstName() + " " + user.getLastName();
    }

    default String contactName(Contact contact) {
        return contact == null ? null : contact.getName();
    }

    default String leadName(Lead lead) {
        if (lead == null) {
            return null;
        }
        String first = lead.getFirstName() == null ? "" : lead.getFirstName();
        String last = lead.getLastName() == null ? "" : lead.getLastName();
        String name = (first + " " + last).trim();
        if (!name.isEmpty()) {
            return name;
        }
        return lead.getCompany();
    }
}

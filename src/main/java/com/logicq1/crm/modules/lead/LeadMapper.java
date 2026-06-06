package com.logicq1.crm.modules.lead;

import com.logicq1.crm.modules.lead.dto.CreateLeadRequest;
import com.logicq1.crm.modules.lead.dto.LeadDTO;
import com.logicq1.crm.modules.lead.dto.UpdateLeadRequest;
import com.logicq1.crm.modules.lead.entity.Lead;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LeadMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "organization", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "converted", ignore = true)
    @Mapping(target = "convertedAt", ignore = true)
    @Mapping(target = "convertedBy", ignore = true)
    @Mapping(target = "convertedContactId", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Lead toEntity(CreateLeadRequest request);

    @BeanMapping(
            nullValuePropertyMappingStrategy =
                    NullValuePropertyMappingStrategy.IGNORE
    )
    void updateLeadFromDto(
            UpdateLeadRequest request,
            @MappingTarget Lead lead
    );

    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "ownerName", expression = "java(getFullName(lead.getOwner()))")
    @Mapping(target = "createdById", source = "createdBy.id")
    @Mapping(target = "createdByName", expression = "java(getFullName(lead.getCreatedBy()))")
    LeadDTO toDTO(Lead lead);

    List<LeadDTO> toDTO(List<Lead> leads);

    default String getFullName(com.logicq1.crm.modules.user.entity.User user) {
        if (user == null) {
            return null;
        }
        return user.getFirstName() + " " + user.getLastName();
    }
}
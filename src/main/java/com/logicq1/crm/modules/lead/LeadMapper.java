package com.logicq1.crm.modules.lead;

import com.logicq1.crm.modules.lead.dto.CreateLeadRequest;
import com.logicq1.crm.modules.lead.dto.UpdateLeadRequest;
import com.logicq1.crm.modules.lead.entity.Lead;
import org.mapstruct.*;

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
}
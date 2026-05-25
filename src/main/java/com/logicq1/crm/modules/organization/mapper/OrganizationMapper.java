package com.logicq1.crm.modules.organization.mapper;

import com.logicq1.crm.modules.organization.dto.CreateOrganizationDto;
import com.logicq1.crm.modules.organization.dto.OrganizationDto;
import com.logicq1.crm.modules.organization.entity.Organization;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrganizationMapper {

    public Organization toOrganization(CreateOrganizationDto organizationDto);

    public OrganizationDto toOrganizationDto(Organization organization);
}

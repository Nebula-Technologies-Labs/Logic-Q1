package com.logicq1.crm.modules.organization.service;

import com.logicq1.crm.modules.organization.dto.OrganizationDto;
import com.logicq1.crm.modules.organization.mapper.OrganizationMapper;
import com.logicq1.crm.modules.organization.repository.OrganizationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class OrganizationService {

    private OrganizationRepository organizationRepository;
    private OrganizationMapper organizationMapper;

    public List<OrganizationDto> findAllOrganizations(){
        return organizationRepository.findAll().stream().map(organizationMapper::toOrganizationDto).toList();
    }
}

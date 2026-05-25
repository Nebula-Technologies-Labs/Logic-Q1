package com.logicq1.crm.modules.organization.service;

import com.logicq1.crm.modules.organization.dto.CreateOrganizationDto;
import com.logicq1.crm.modules.organization.dto.OrganizationDto;
import com.logicq1.crm.modules.organization.entity.Organization;
import com.logicq1.crm.modules.organization.mapper.OrganizationMapper;
import com.logicq1.crm.modules.organization.repository.OrganizationRepository;
import com.logicq1.crm.modules.user.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class OrganizationService {

    private OrganizationRepository organizationRepository;
    private OrganizationMapper organizationMapper;

    public OrganizationDto createOrganization(CreateOrganizationDto organizationDto, User user) {
        Organization organization = organizationMapper.toOrganization(organizationDto);
        organization.setAdminId(user);
        return organizationMapper.toOrganizationDto(organizationRepository.save(organization));
    }

    public OrganizationDto findOrganizationById(String id) {
        return organizationMapper.toOrganizationDto(organizationRepository.findById(id).orElseThrow(() -> new RuntimeException("Organization not found")));
    }

    public List<OrganizationDto> findAllOrganizations(){
        return organizationRepository.findAll().stream().map(organizationMapper::toOrganizationDto).toList();
    }
}

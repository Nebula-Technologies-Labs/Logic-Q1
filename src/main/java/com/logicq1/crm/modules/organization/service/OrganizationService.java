package com.logicq1.crm.modules.organization.service;

import com.logicq1.crm.modules.organization.dto.CreateOrganizationDto;
import com.logicq1.crm.modules.organization.dto.OrganizationDto;
import com.logicq1.crm.modules.organization.entity.Organization;
import com.logicq1.crm.modules.organization.mapper.OrganizationMapper;
import com.logicq1.crm.modules.organization.repository.OrganizationRepository;
import com.logicq1.crm.modules.user.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class OrganizationService {

    private OrganizationRepository organizationRepository;
    private OrganizationMapper organizationMapper;

    public OrganizationDto createOrganization(CreateOrganizationDto organizationDto, User user) {
        if (organizationRepository.existsByAdminId(user)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "You already own an organization");
        }
        Organization organization = organizationMapper.toOrganization(organizationDto);
        organization.setAdminId(user);
        return organizationMapper.toOrganizationDto(organizationRepository.save(organization));
    }

    public OrganizationDto findOrganizationById(String id) {
        UUID organizationId;
        try {
            organizationId = UUID.fromString(id);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid organization id: '" + id + "' is not a valid UUID");
        }
        return organizationMapper.toOrganizationDto(organizationRepository.findById(organizationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Organization not found")));
    }

    public List<OrganizationDto> findAllOrganizations(){
        return organizationRepository.findAll().stream().map(organizationMapper::toOrganizationDto).toList();
    }
}

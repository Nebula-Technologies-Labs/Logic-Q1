package com.logicq1.crm.modules.organization.service;

import com.logicq1.crm.modules.organization.dto.CreateOrganizationDto;
import com.logicq1.crm.modules.organization.dto.OrganizationDto;
import com.logicq1.crm.modules.organization.entity.Organization;
import com.logicq1.crm.modules.organization.mapper.OrganizationMapper;
import com.logicq1.crm.modules.organization.repository.OrganizationRepository;
import com.logicq1.crm.modules.user.entity.User;
import com.logicq1.crm.modules.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = OrganizationService.class)
class OrganizationServiceTest {

    @Autowired
    private OrganizationService organizationService;

    @MockitoBean
    private OrganizationRepository organizationRepository;
    @MockitoBean
    private OrganizationMapper organizationMapper;
    @MockitoBean
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder().id(UUID.randomUUID()).build();
    }

    private OrganizationDto sampleDto(UUID id) {
        return new OrganizationDto(
                id, "Acme", "desc", "US", "NY", "NYC",
                "10001", "SaaS", "12345", "https://acme.com", "2026-06-12"
        );
    }

    @Test
    void createOrganization_savesOrg_assignsAdmin_andLinksUser() {
        CreateOrganizationDto dto =
                new CreateOrganizationDto("Acme", "SaaS", "US", "https://acme.com");
        Organization mapped = Organization.builder().name("Acme").build();
        Organization saved = Organization.builder().id(UUID.randomUUID()).name("Acme").build();
        OrganizationDto responseDto = sampleDto(saved.getId());

        when(organizationRepository.existsByAdminId(user)).thenReturn(false);
        when(organizationMapper.toOrganization(dto)).thenReturn(mapped);
        when(organizationRepository.save(mapped)).thenReturn(saved);
        when(organizationMapper.toOrganizationDto(saved)).thenReturn(responseDto);

        OrganizationDto result = organizationService.createOrganization(dto, user);

        assertThat(result).isEqualTo(responseDto);
        assertThat(mapped.getAdminId()).isEqualTo(user);
        assertThat(user.getOrganization()).isEqualTo(saved);
        verify(userRepository).save(user);
    }

    @Test
    void createOrganization_throwsConflict_whenUserAlreadyOwnsOrganization() {
        CreateOrganizationDto dto =
                new CreateOrganizationDto("Acme", "SaaS", "US", "https://acme.com");

        when(organizationRepository.existsByAdminId(user)).thenReturn(true);

        assertThatThrownBy(() -> organizationService.createOrganization(dto, user))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.CONFLICT);

        verify(organizationRepository, never()).save(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void findOrganizationById_returnsMappedDto_whenFound() {
        UUID id = UUID.randomUUID();
        Organization organization = Organization.builder().id(id).name("Acme").build();
        OrganizationDto dto = sampleDto(id);

        when(organizationRepository.findById(id)).thenReturn(Optional.of(organization));
        when(organizationMapper.toOrganizationDto(organization)).thenReturn(dto);

        assertThat(organizationService.findOrganizationById(id.toString())).isEqualTo(dto);
    }

    @Test
    void findOrganizationById_throwsBadRequest_whenIdIsNotUuid() {
        assertThatThrownBy(() -> organizationService.findOrganizationById("not-a-uuid"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void findOrganizationById_throwsNotFound_whenMissing() {
        UUID id = UUID.randomUUID();
        when(organizationRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> organizationService.findOrganizationById(id.toString()))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void findAllOrganizations_mapsEveryOrganization() {
        when(organizationRepository.findAll())
                .thenReturn(List.of(new Organization(), new Organization()));
        when(organizationMapper.toOrganizationDto(any()))
                .thenReturn(sampleDto(UUID.randomUUID()));

        assertThat(organizationService.findAllOrganizations()).hasSize(2);
    }
}

package com.logicq1.crm.modules.organization.service;

import com.logicq1.crm.enums.InviteStatus;
import com.logicq1.crm.enums.UserRole;
import com.logicq1.crm.modules.email.service.EmailService;
import com.logicq1.crm.modules.organization.dto.CreateOrganizationInviteRequest;
import com.logicq1.crm.modules.organization.dto.OrganizationInviteResponse;
import com.logicq1.crm.modules.organization.dto.ValidateOrganizationInviteResponse;
import com.logicq1.crm.modules.organization.entity.Organization;
import com.logicq1.crm.modules.organization.entity.OrganizationInvite;
import com.logicq1.crm.modules.organization.entity.Roles;
import com.logicq1.crm.modules.organization.repository.OrganizationInviteRepository;
import com.logicq1.crm.modules.organization.repository.RolesRepository;
import com.logicq1.crm.modules.user.entity.User;
import com.logicq1.crm.modules.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = OrganizationInviteService.class)
class OrganizationInviteServiceTest {

    @Autowired
    private OrganizationInviteService inviteService;

    @MockitoBean
    private OrganizationInviteRepository inviteRepository;
    @MockitoBean
    private UserRepository userRepository;
    @MockitoBean
    private RolesRepository rolesRepository;
    @MockitoBean
    private EmailService emailService;

    private Organization organization;
    private User admin;
    private Roles role;

    @BeforeEach
    void setUp() {
        organization = Organization.builder()
                .id(UUID.randomUUID())
                .name("Acme")
                .build();

        admin = User.builder()
                .id(UUID.randomUUID())
                .organization(organization)
                .build();

        role = Roles.builder()
                .id(UUID.randomUUID())
                .role(UserRole.MEMBER)
                .build();
    }

    private CreateOrganizationInviteRequest inviteRequest(String email) {
        CreateOrganizationInviteRequest request = new CreateOrganizationInviteRequest();
        request.setEmail(email);
        request.setRoleId(role.getId());
        return request;
    }

    @Test
    void createInvite_persistsPendingInvite_sendsEmail_andReturnsToken() {
        CreateOrganizationInviteRequest request = inviteRequest("invitee@acme.com");

        when(userRepository.findByEmail("invitee@acme.com")).thenReturn(Optional.empty());
        when(inviteRepository.existsByEmailAndOrganizationAndStatus(
                "invitee@acme.com", organization, InviteStatus.PENDING)).thenReturn(false);
        when(rolesRepository.findById(role.getId())).thenReturn(Optional.of(role));
        when(inviteRepository.save(any(OrganizationInvite.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        OrganizationInviteResponse response = inviteService.createInvite(request, admin);

        assertThat(response.getToken()).isNotBlank();

        verify(inviteRepository).save(any(OrganizationInvite.class));
        verify(emailService).sendSimpleMessage(any());
    }

    @Test
    void createInvite_throwsConflict_whenUserAlreadyBelongsToAnOrganization() {
        CreateOrganizationInviteRequest request = inviteRequest("taken@acme.com");
        User existing = User.builder()
                .id(UUID.randomUUID())
                .organization(Organization.builder().id(UUID.randomUUID()).build())
                .build();

        when(userRepository.findByEmail("taken@acme.com")).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> inviteService.createInvite(request, admin))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.CONFLICT);

        verify(inviteRepository, never()).save(any());
        verify(emailService, never()).sendSimpleMessage(any());
    }

    @Test
    void createInvite_throwsConflict_whenPendingInviteAlreadyExists() {
        CreateOrganizationInviteRequest request = inviteRequest("invitee@acme.com");

        when(userRepository.findByEmail("invitee@acme.com")).thenReturn(Optional.empty());
        when(inviteRepository.existsByEmailAndOrganizationAndStatus(
                "invitee@acme.com", organization, InviteStatus.PENDING)).thenReturn(true);

        assertThatThrownBy(() -> inviteService.createInvite(request, admin))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.CONFLICT);

        verify(inviteRepository, never()).save(any());
    }

    @Test
    void createInvite_throwsNotFound_whenRoleDoesNotExist() {
        CreateOrganizationInviteRequest request = inviteRequest("invitee@acme.com");

        when(userRepository.findByEmail("invitee@acme.com")).thenReturn(Optional.empty());
        when(inviteRepository.existsByEmailAndOrganizationAndStatus(
                "invitee@acme.com", organization, InviteStatus.PENDING)).thenReturn(false);
        when(rolesRepository.findById(role.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> inviteService.createInvite(request, admin))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);

        verify(inviteRepository, never()).save(any());
    }

    @Test
    void validateInvite_returnsInviteDetails_whenPendingAndNotExpired() {
        OrganizationInvite invite = OrganizationInvite.builder()
                .token("tok-1")
                .email("invitee@acme.com")
                .organization(organization)
                .role(role)
                .invitedBy(admin)
                .status(InviteStatus.PENDING)
                .expiresAt(LocalDateTime.now().plusDays(1))
                .build();

        when(inviteRepository.findByToken("tok-1")).thenReturn(Optional.of(invite));

        ValidateOrganizationInviteResponse response = inviteService.validateInvite("tok-1");

        assertThat(response.getEmail()).isEqualTo("invitee@acme.com");
        assertThat(response.getOrganizationName()).isEqualTo("Acme");
        assertThat(response.getRole()).isEqualTo(UserRole.MEMBER.name());
        assertThat(response.getStatus()).isEqualTo(InviteStatus.PENDING);
    }

    @Test
    void validateInvite_throwsNotFound_whenTokenUnknown() {
        when(inviteRepository.findByToken("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> inviteService.validateInvite("missing"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void validateInvite_throwsBadRequest_whenInviteNotPending() {
        OrganizationInvite invite = OrganizationInvite.builder()
                .token("tok-2")
                .status(InviteStatus.ACCEPTED)
                .expiresAt(LocalDateTime.now().plusDays(1))
                .build();

        when(inviteRepository.findByToken("tok-2")).thenReturn(Optional.of(invite));

        assertThatThrownBy(() -> inviteService.validateInvite("tok-2"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void validateInvite_marksExpiredAndThrows_whenPastExpiry() {
        OrganizationInvite invite = OrganizationInvite.builder()
                .token("tok-3")
                .status(InviteStatus.PENDING)
                .expiresAt(LocalDateTime.now().minusDays(1))
                .build();

        when(inviteRepository.findByToken("tok-3")).thenReturn(Optional.of(invite));

        assertThatThrownBy(() -> inviteService.validateInvite("tok-3"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);

        assertThat(invite.getStatus()).isEqualTo(InviteStatus.EXPIRED);
        verify(inviteRepository).save(invite);
    }
}

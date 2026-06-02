package com.logicq1.crm.modules.organization.service;

import com.logicq1.crm.modules.email.dto.MailBody;
import com.logicq1.crm.modules.email.service.EmailService;
import com.logicq1.crm.modules.organization.dto.CreateOrganizationInviteRequest;
import com.logicq1.crm.modules.organization.dto.OrganizationInviteResponse;
import com.logicq1.crm.modules.organization.dto.ValidateOrganizationInviteResponse;
import com.logicq1.crm.modules.organization.entity.OrganizationInvite;
import com.logicq1.crm.modules.organization.entity.Roles;
import com.logicq1.crm.enums.InviteStatus;
import com.logicq1.crm.modules.organization.repository.OrganizationInviteRepository;
import com.logicq1.crm.modules.organization.repository.RolesRepository;
import com.logicq1.crm.modules.user.entity.User;
import com.logicq1.crm.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrganizationInviteService {

    private final OrganizationInviteRepository organizationInviteRepository;
    private final UserRepository userRepository;
    private final RolesRepository rolesRepository;
    private final EmailService emailService;

    public OrganizationInviteResponse createInvite(
            CreateOrganizationInviteRequest request,
            User admin
    ) {

        User existingUser = userRepository
                .findByEmail(request.getEmail())
                .orElse(null);

        if(existingUser != null &&
                existingUser.getOrganization() != null) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "User already belongs to an organization"
            );
        }

        if(organizationInviteRepository
                .existsByEmailAndOrganizationAndStatus(
                        request.getEmail(),
                        admin.getOrganization(),
                        InviteStatus.PENDING
                )) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Pending invite already exists"
            );
        }

        Roles role = rolesRepository
                .findById(request.getRoleId())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Role not found"
                        )
                );

        String token = UUID.randomUUID().toString();

        OrganizationInvite invite = OrganizationInvite.builder()
                .token(token)
                .email(request.getEmail())
                .role(role)
                .organization(admin.getOrganization())
                .invitedBy(admin)
                .status(InviteStatus.PENDING)
                .expiresAt(LocalDateTime.now().plusDays(7))
                .createdAt(LocalDateTime.now())
                .build();

        OrganizationInvite savedInvite =
                organizationInviteRepository.save(invite);

        String inviteLink =
                "http://localhost:3000/invite/" + token;

        MailBody mailBody = new MailBody(
                request.getEmail(),
                "Invitation to join "
                        + admin.getOrganization().getName(),
                "You have been invited to join "
                        + admin.getOrganization().getName()
                        + ".\n\nClick the link below to register:\n\n"
                        + inviteLink
        );

        emailService.sendSimpleMessage(mailBody);

        return OrganizationInviteResponse.builder()
                .token(savedInvite.getToken())
                .build();
    }

    @Transactional
    public ValidateOrganizationInviteResponse validateInvite(
            String token
    ) {

        OrganizationInvite invite =
                organizationInviteRepository.findByToken(token)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Invite not found"
                                ));

        if(invite.getStatus() != InviteStatus.PENDING) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invite is no longer valid"
            );
        }

        if(invite.getExpiresAt().isBefore(LocalDateTime.now())) {

            invite.setStatus(InviteStatus.EXPIRED);

            organizationInviteRepository.save(invite);

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invite expired"
            );
        }

        return ValidateOrganizationInviteResponse.builder()
                .email(invite.getEmail())
                .organizationName(invite.getOrganization().getName())
                .role(invite.getRole().getRole().name())
                .status(invite.getStatus())
                .expiresAt(invite.getExpiresAt())
                .build();
    }
}
package com.logicq1.crm.modules.organization.controller;

import com.logicq1.crm.modules.organization.dto.CreateOrganizationInviteRequest;
import com.logicq1.crm.modules.organization.dto.OrganizationInviteResponse;
import com.logicq1.crm.modules.organization.dto.ValidateOrganizationInviteResponse;
import com.logicq1.crm.modules.organization.service.OrganizationInviteService;
import com.logicq1.crm.modules.user.entity.User;
import com.logicq1.crm.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/organization/invites")
@RequiredArgsConstructor
public class OrganizationInviteController {

    private final OrganizationInviteService organizationInviteService;
    private  final UserRepository userRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrganizationInviteResponse createInvite(
            @RequestBody CreateOrganizationInviteRequest request,
            Authentication authentication
    ) {

        String email = authentication.getName();

        User admin = userRepository.findByEmail(email)
                .orElseThrow();

        return organizationInviteService.createInvite(
                request,
                admin
        );
    }
    @GetMapping("/{token}")
    public ValidateOrganizationInviteResponse validateInvite(
            @PathVariable String token
    ) {

        return organizationInviteService.validateInvite(token);
    }
}
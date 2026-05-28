package com.logicq1.crm.modules.auth.service;

import com.logicq1.crm.modules.auth.dto.RegisterInviteRequest;
import com.logicq1.crm.modules.auth.dto.RegisterRequest;
import com.logicq1.crm.modules.auth.enums.AuthProvider;
import com.logicq1.crm.modules.organization.enums.UserRole;
import com.logicq1.crm.modules.organization.entity.OrganizationInvite;
import com.logicq1.crm.modules.organization.enums.InviteStatus;
import com.logicq1.crm.modules.organization.repository.OrganizationInviteRepository;
import com.logicq1.crm.modules.user.entity.User;
import com.logicq1.crm.modules.user.mapper.UserMapper;
import com.logicq1.crm.modules.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final OrganizationInviteRepository organizationInviteRepository;

    public void registerUser(RegisterRequest data) throws RuntimeException{
        Optional<User> userExists = userRepository.findByEmail(data.getEmail());

        if(userExists.isPresent()){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }

        User user = userMapper.toUser(data);
        user.setPassword(passwordEncoder.encode(data.getPassword()));
        user.setOnBoarded(false);
        user.setAuthBy(AuthProvider.LOCAL);
        user.setRole(UserRole.MEMBER);
        userRepository.save(user);
    }
    @Transactional
    public void registerInvite(
            RegisterInviteRequest request
    ) {

        OrganizationInvite invite =
                organizationInviteRepository
                        .findByToken(request.getToken())
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

        boolean userExists =
                userRepository.findByEmail(invite.getEmail()).isPresent();

        if(userExists) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "User already exists"
            );
        }

        User user = User.builder()
                .email(invite.getEmail())
                .name(request.getName())
                .password(passwordEncoder.encode(request.getPassword()))
                .organization(invite.getOrganization())
                .role(invite.getRole())
                .createdBy(invite.getInvitedBy())
                .onBoarded(true)
                .build();

        userRepository.save(user);

        invite.setStatus(InviteStatus.ACCEPTED);

        organizationInviteRepository.save(invite);
    }

}

package com.logicq1.crm.modules.auth.service;

import com.logicq1.crm.enums.AuthProvider;
import com.logicq1.crm.enums.InviteStatus;
import com.logicq1.crm.enums.UserRole;
import com.logicq1.crm.modules.auth.dto.AuthResponse;
import com.logicq1.crm.modules.auth.dto.LoginRequest;
import com.logicq1.crm.modules.auth.dto.RegisterInviteRequest;
import com.logicq1.crm.modules.auth.dto.RegisterRequest;
import com.logicq1.crm.modules.organization.entity.OrganizationInvite;
import com.logicq1.crm.modules.organization.entity.Roles;
import com.logicq1.crm.modules.organization.repository.OrganizationInviteRepository;
import com.logicq1.crm.modules.organization.repository.RolesRepository;
import com.logicq1.crm.modules.user.entity.User;
import com.logicq1.crm.modules.user.mapper.UserMapper;
import com.logicq1.crm.modules.user.repository.UserRepository;
import com.logicq1.crm.services.jwt.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
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
    private final RolesRepository rolesRepository;
    private final AuthenticationManager authManager;
    private final JwtUtil jwtService;


    public AuthResponse registerUser(
            RegisterRequest data
    ) {
        Optional<User> userExists =
                userRepository.findByEmail(
                        data.getEmail()
                );
        if (
                userExists.isPresent()
        ) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email already in use"
            );
        }

        Roles memberRole =
                rolesRepository
                        .findByRole(
                                UserRole.MEMBER
                        )
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Default MEMBER role not found"
                                )
                        );

        User user =
                userMapper.toUser(
                        data
                );

        user.setPassword(
                passwordEncoder.encode(
                        data.getPassword()
                )
        );

        user.setOnBoarded(
                false
        );

        user.setAuthBy(
                AuthProvider.LOCAL
        );

        user.setIsActive(
                true
        );

        user.setEmailVerified(
                false
        );

        user.setRole(
                memberRole
        );

        user =
                userRepository.save(
                        user
                );

        String token =
                jwtService.generateToken(
                        user.getEmail()
                );
        return AuthResponse.builder()

                .status(true)
                .message(
                        "User registered successfully"
                )
                .token(
                        token
                )
                .user(
                        buildUserData(
                                user
                        )
                )
                .build();
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
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .password(passwordEncoder.encode(request.getPassword()))
                .organization(invite.getOrganization())
                .role(invite.getRole())
                .createdBy(invite.getInvitedBy())
                .onBoarded(true)
                .isActive(true)
                .emailVerified(true)
                .authBy(AuthProvider.LOCAL)
                .build();

        userRepository.save(user);

        invite.setStatus(InviteStatus.ACCEPTED);

        organizationInviteRepository.save(invite);
    }
    public AuthResponse login(
            LoginRequest request
    ) {

        authManager.authenticate(

                new UsernamePasswordAuthenticationToken(

                        request.getEmail(),

                        request.getPassword()

                )

        );

        User user =
                userRepository

                        .findByEmail(
                                request.getEmail()
                        )

                        .orElseThrow(
                                () -> new UsernameNotFoundException(
                                        "User not found"
                                )
                        );

        userRepository.updateLastLogin(

                request.getEmail(),

                LocalDateTime.now()

        );

        String token =
                jwtService.generateToken(
                        request.getEmail()
                );

        return AuthResponse.builder()

                .status(true)

                .message(
                        "Login successful"
                )

                .token(
                        token
                )

                .user(
                        buildUserData(
                                user
                        )
                )

                .build();
    }

    public AuthResponse getCurrentUser() {

        Authentication authentication =

                SecurityContextHolder

                        .getContext()

                        .getAuthentication();

        if (

                authentication == null

                        ||

                        !authentication.isAuthenticated()

                        ||

                        authentication instanceof AnonymousAuthenticationToken

        ) {

            throw new BadCredentialsException(
                    "Unauthenticated"
            );

        }

        String email =
                authentication.getName();

        User user =
                userRepository

                        .findByEmail(
                                email
                        )

                        .orElseThrow(
                                () -> new UsernameNotFoundException(
                                        "User not found"
                                )
                        );

        return AuthResponse.builder()

                .status(true)

                .message(
                        "User fetched successfully"
                )

                .user(
                        buildUserData(
                                user
                        )
                )

                .build();
    }

    private AuthResponse.UserData buildUserData(
            User user
    ) {

        return AuthResponse.UserData.builder()

                .id(user.getId().toString())

                .email(user.getEmail())

                .firstName(user.getFirstName())

                .lastName(user.getLastName())

                .profileImage(user.getProfileImage())

                .jobTitle(user.getJobTitle())

                .phoneNumber(user.getPhoneNumber())

                .department(user.getDepartment())

                .onBoarded(user.getOnBoarded())

                .emailVerified(user.getEmailVerified())

                .orgId(
                        user.getOrganization() != null
                                ? user.getOrganization().getId()
                                : null
                )

                .roleId(
                        user.getRole() != null
                                ? user.getRole().getId()
                                : null
                )

                .build();
    }
    @Transactional
    public User handleGoogleLogin(
            OAuth2User oauthUser
    ) {

        String email =
                oauthUser.getAttribute(
                        "email"
                );

        return userRepository

                .findByEmail(email)

                .orElseGet(() -> {

                    Roles memberRole =

                            rolesRepository

                                    .findByRole(
                                            UserRole.MEMBER
                                    )

                                    .orElseThrow(
                                            () -> new RuntimeException(
                                                    "Default MEMBER role not found"
                                            )
                                    );

                    User user =

                            User.builder()

                                    .email(email)

                                    .firstName(
                                            oauthUser.getAttribute(
                                                    "given_name"
                                            )
                                    )

                                    .lastName(
                                            oauthUser.getAttribute(
                                                    "family_name"
                                            )
                                    )

                                    .profileImage(
                                            oauthUser.getAttribute(
                                                    "picture"
                                            )
                                    )

                                    .authBy(
                                            AuthProvider.GOOGLE
                                    )

                                    .emailVerified(
                                            true
                                    )

                                    .onBoarded(
                                            false
                                    )

                                    .isActive(
                                            true
                                    )

                                    .role(
                                            memberRole
                                    )

                                    .build();

                    return userRepository.save(
                            user
                    );
                });

    }

}

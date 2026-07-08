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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
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
    private final TokenService tokenService;
    private final CookieService cookieService;
    private final CurrentUserService currentUserService;


    public AuthResponse registerUser(
            RegisterRequest data,
            HttpServletResponse response
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

        Roles defaultRole =
                rolesRepository
                        .findByRole(
                                UserRole.ORG_ADMIN
                        )
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Default ORG_ADMIN role not found"
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
                defaultRole
        );

        user =
                userRepository.save(
                        user
                );



        TokenService.TokenPair tokenPair =
                tokenService.createTokenPair(user);

        cookieService.addRefreshCookie(
                response,
                tokenPair.refreshToken()
        );

        return AuthResponse.builder()
                .status(true)
                .message(
                        "User registered successfully"
                )
                .accessToken(
                        tokenPair.accessToken()
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
            LoginRequest request,
            HttpServletResponse response
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

        TokenService.TokenPair tokenPair =
                tokenService.createTokenPair(user);

        cookieService.addRefreshCookie(
                response,
                tokenPair.refreshToken()
        );

        return AuthResponse.builder()
                .status(true)
                .message(
                        "Login successful"
                )
                .accessToken(
                        tokenPair.accessToken()
                )
                .user(
                        buildUserData(
                                user
                        )
                )
                .build();
    }

    public AuthResponse getCurrentUser() {

        User user =
                currentUserService.getCurrentUser();

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
    public AuthResponse refresh(
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        String refreshToken =
                cookieService.extractRefreshToken(request);

        if (refreshToken == null) {
            throw new BadCredentialsException(
                    "Refresh token missing"
            );
        }

        TokenService.TokenPair tokenPair =
                tokenService.refresh(refreshToken);

        cookieService.addRefreshCookie(
                response,
                tokenPair.refreshToken()
        );

        User user = userRepository
                .findByEmail(
                        jwtService.extractUsername(
                                tokenPair.accessToken()
                        )
                )
                .orElseThrow();

        return AuthResponse.builder()
                .status(true)
                .message("Token refreshed")
                .accessToken(tokenPair.accessToken())
                .user(buildUserData(user))
                .build();
    }

    public void logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        String refreshToken =
                cookieService.extractRefreshToken(request);

        if (refreshToken != null) {
            tokenService.logout(refreshToken);
        }

        cookieService.clearRefreshCookie(response);
    }

    public void logoutAll(
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        User user = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow();

        tokenService.logoutAll(user);

        cookieService.clearRefreshCookie(response);
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

package com.logicq1.crm.modules.auth.service;

import com.logicq1.crm.enums.AuthProvider;
import com.logicq1.crm.enums.UserRole;
import com.logicq1.crm.modules.organization.entity.Roles;
import com.logicq1.crm.modules.organization.repository.RolesRepository;
import com.logicq1.crm.modules.user.entity.User;
import com.logicq1.crm.modules.user.repository.UserRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OAuthService {

    private final UserRepository userRepository;
    private final RolesRepository rolesRepository;

    public OAuthService(UserRepository userRepository, RolesRepository rolesRepository) {
        this.userRepository = userRepository;
        this.rolesRepository = rolesRepository;
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

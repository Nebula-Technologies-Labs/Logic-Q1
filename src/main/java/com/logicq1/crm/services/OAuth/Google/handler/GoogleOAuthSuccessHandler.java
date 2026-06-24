package com.logicq1.crm.services.OAuth.Google.handler;

import com.logicq1.crm.modules.auth.service.OAuthService;
import com.logicq1.crm.modules.user.entity.User;
import com.logicq1.crm.services.jwt.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Service
public class GoogleOAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final OAuthService oAuthService;
    @Value("${app.frontend-url}")
    private final String frontendUrl;

    public GoogleOAuthSuccessHandler(
            JwtUtil jwtUtil, OAuthService oAuthService,
            @Value("${app.frontend-url}") String frontendUrl
    ) {
        this.jwtUtil = jwtUtil;
        this.oAuthService = oAuthService;
        this.frontendUrl = frontendUrl;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oauthUser =
                (OAuth2User)
                        authentication.getPrincipal();

        assert oauthUser != null;
        User user =

                oAuthService

                        .handleGoogleLogin(
                                oauthUser
                        );

        String token =

                jwtUtil.generateToken(
                        user.getEmail()
                );

        String targetUrl = UriComponentsBuilder

                .fromUriString(
                        frontendUrl + "/oauth2/redirect"
                )
                .queryParam(
                        "token",
                        token
                )
                .build()
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }


}

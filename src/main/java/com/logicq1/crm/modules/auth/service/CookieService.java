package com.logicq1.crm.modules.auth.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class CookieService {

    public static final String REFRESH_TOKEN_COOKIE = "refreshToken";

    @Value("${app.cookie.secure:true}")
    private boolean secureCookie;

    public void addRefreshCookie(
            HttpServletResponse response,
            String refreshToken
    ) {

        ResponseCookie cookie = ResponseCookie
                .from(
                        REFRESH_TOKEN_COOKIE,
                        refreshToken
                )
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(Duration.ofDays(30))
                .build();

        response.addHeader(
                "Set-Cookie",
                cookie.toString()
        );
    }

    public void clearRefreshCookie(
            HttpServletResponse response
    ) {

        ResponseCookie cookie = ResponseCookie
                .from(
                        REFRESH_TOKEN_COOKIE,
                        ""
                )
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(0)
                .build();

        response.addHeader(
                "Set-Cookie",
                cookie.toString()
        );
    }

    public String extractRefreshToken(
            HttpServletRequest request
    ) {

        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {

            if (REFRESH_TOKEN_COOKIE.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }
}

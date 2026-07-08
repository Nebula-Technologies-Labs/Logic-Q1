package com.logicq1.crm.modules.auth.service;

import com.logicq1.crm.modules.auth.entity.RefreshToken;
import com.logicq1.crm.modules.auth.repositories.RefreshTokenRepository;
import com.logicq1.crm.modules.user.entity.User;
import com.logicq1.crm.services.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.util.List;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class TokenService {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    public TokenPair createTokenPair(User user) {

        String accessToken =
                jwtUtil.generateAccessToken(user);

        String refreshToken =
                jwtUtil.generateRefreshToken(user);

        RefreshToken refreshTokenEntity =
                RefreshToken.builder()
                        .token(refreshToken)
                        .user(user)
                        .expiresAt(LocalDateTime.now().plusDays(30))
                        .revoked(false)
                        .used(false)
                        .build();

        refreshTokenRepository.save(refreshTokenEntity);

        return new TokenPair(
                accessToken,
                refreshToken
        );
    }

    public record TokenPair(
            String accessToken,
            String refreshToken
    ) {
    }

    public TokenPair refresh(String refreshTokenValue) {

        RefreshToken storedToken =
                refreshTokenRepository
                        .findByToken(refreshTokenValue)
                        .orElseThrow(() ->
                                new BadCredentialsException(
                                        "Invalid refresh token"
                                )
                        );

        detectTokenReuse(storedToken);

        if (storedToken.isRevoked()) {
            throw new BadCredentialsException(
                    "Refresh token revoked"
            );
        }

        if (storedToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadCredentialsException(
                    "Refresh token expired"
            );
        }

        storedToken.setUsed(true);
        storedToken.setRevoked(true);

        refreshTokenRepository.save(storedToken);

        return createTokenPair(
                storedToken.getUser()
        );
    }

    public void logout(String refreshTokenValue) {

        refreshTokenRepository
                .findByToken(refreshTokenValue)
                .ifPresent(this::revokeToken);
    }

    public void logoutAll(User user) {

        List<RefreshToken> tokens =
                refreshTokenRepository
                        .findByUserAndRevokedFalse(user);

        tokens.forEach(this::revokeToken);
    }

    public void revokeToken(RefreshToken refreshToken) {

        refreshToken.setRevoked(true);
        refreshToken.setUsed(true);

        refreshTokenRepository.save(refreshToken);
    }

    public void detectTokenReuse(RefreshToken refreshToken) {

        if (refreshToken.isUsed()) {

            User user = refreshToken.getUser();

            logoutAll(user);

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Refresh token reuse detected. All sessions revoked."
            );
        }
    }
}

package com.logicq1.crm.modules.auth.controller;

import com.logicq1.crm.modules.auth.dto.AuthResponse;
import com.logicq1.crm.modules.auth.dto.LoginRequest;
import com.logicq1.crm.modules.auth.dto.RegisterInviteRequest;
import com.logicq1.crm.modules.auth.dto.RegisterRequest;
import com.logicq1.crm.modules.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;


    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @RequestBody RegisterRequest request,
            HttpServletResponse response
    ) {
        return ResponseEntity
                .status(
                        HttpStatus.CREATED
                )
                .body(
                        authService.registerUser(
                                request,
                                response
                        )
                );
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
        try {
            return ResponseEntity.ok(
                    authService.login(
                            request,
                            response
                    )
            );
        }
        catch (
                AuthenticationException e
        ) {
            return ResponseEntity
                    .status(
                            HttpStatus.UNAUTHORIZED
                    )
                    .body(
                            Map.of(
                                    "status", false,
                                    "message", "Invalid credentials"
                            )
                    );
        }
    }

    @PostMapping("/register-invite")
    @ResponseStatus(HttpStatus.CREATED)
    public String registerInvite(
            @RequestBody RegisterInviteRequest request
    ) {

        authService.registerInvite(request);

        return "User registered successfully";
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMe(){
        return ResponseEntity.ok(authService.getCurrentUser());
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        return ResponseEntity.ok(
                authService.refresh(
                        request,
                        response
                )
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        authService.logout(
                request,
                response
        );

        return ResponseEntity.ok(
                Map.of(
                        "status", true,
                        "message", "Logged out successfully"
                )
        );
    }

    @PostMapping("/logout-all")
    public ResponseEntity<?> logoutAll(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        authService.logoutAll(
                request,
                response
        );

        return ResponseEntity.ok(
                Map.of(
                        "status", true,
                        "message", "Logged out from all devices"
                )
        );
    }
}
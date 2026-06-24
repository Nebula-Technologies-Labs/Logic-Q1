package com.logicq1.crm.modules.auth.controller;

import com.logicq1.crm.modules.auth.dto.AuthResponse;
import com.logicq1.crm.modules.auth.dto.LoginRequest;
import com.logicq1.crm.modules.auth.dto.RegisterInviteRequest;
import com.logicq1.crm.modules.auth.dto.RegisterRequest;
import com.logicq1.crm.modules.auth.service.AuthService;
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
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity
                .status(
                        HttpStatus.CREATED
                )
                .body(
                        authService.registerUser(
                                request
                        )
                );
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request
    ) {
        try {
            return ResponseEntity.ok(
                    authService.login(
                            request
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

}
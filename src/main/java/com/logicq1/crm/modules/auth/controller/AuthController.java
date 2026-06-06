package com.logicq1.crm.modules.auth.controller;

import com.logicq1.crm.modules.auth.dto.LoginRequest;
import com.logicq1.crm.modules.auth.dto.RegisterInviteRequest;
import com.logicq1.crm.modules.auth.dto.RegisterRequest;
import com.logicq1.crm.modules.auth.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;


    @PostMapping("/register")
    private ResponseEntity<Map<String, Object>> register(@RequestBody RegisterRequest data) {
        HashMap<String,Object> response = new HashMap<>();
        authService.registerUser(data);
        response.put("message", "User registered successfully");
        response.put("status", true);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request
    ) {
        try {
            return ResponseEntity.ok(
                    authService.login(request)
            );
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "message", "Invalid credentials",
                            "status", false
                    ));
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

}
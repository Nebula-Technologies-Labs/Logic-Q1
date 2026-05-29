package com.logicq1.crm.modules.auth.controller;

import com.logicq1.crm.modules.auth.dto.RegisterInviteRequest;
import com.logicq1.crm.modules.auth.dto.RegisterRequest;
import com.logicq1.crm.modules.auth.service.AuthService;
import com.logicq1.crm.modules.user.entity.User;
import com.logicq1.crm.services.jwt.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authManager;
    private JwtUtil jwtService;


    @PostMapping("/register")
    private ResponseEntity<Map<String, Object>> register(@RequestBody RegisterRequest data) {
        HashMap<String,Object> response = new HashMap<>();
        authService.registerUser(data);
        response.put("message", "User registered successfully");
        response.put("status", true);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {

        try {
            Authentication authentication =
                    authManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    user.getUsername(),
                                    user.getPassword()
                            ));

            return ResponseEntity.ok(
                    jwtService.generateToken(user.getUsername())
            );

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid credentials");
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
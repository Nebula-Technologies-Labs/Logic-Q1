package com.logicq1.crm.controller;

import com.logicq1.crm.dto.LoginRequest;
import com.logicq1.crm.dto.RegisterRequest;
import com.logicq1.crm.entity.User;
import com.logicq1.crm.repository.UserRepository;
import com.logicq1.crm.service.AuthService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String home() {
        return "CRM Backend Running";
    }
    @PostMapping("/register")
    public String register(@RequestBody User user) {

        user.setPassword(
                passwordEncoder.encode(user.getPassword())
        );

        userRepository.save(user);

        return "User Registered Successfully";
    }
}
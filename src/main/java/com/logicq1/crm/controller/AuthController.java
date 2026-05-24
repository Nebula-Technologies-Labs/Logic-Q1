package com.logicq1.crm.controller;

import com.logicq1.crm.dto.LoginRequest;
import com.logicq1.crm.dto.RegisterRequest;
import com.logicq1.crm.entity.User;
import com.logicq1.crm.repository.UserRepository;
import com.logicq1.crm.service.AuthService;
import com.logicq1.crm.service.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
    private final AuthenticationManager authManager;
    @Autowired
    private JwtUtil jwtService;

    @GetMapping("/")
    public String home() {
        return "CRM Backend Running";
    }
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {

        user.setPassword(
                passwordEncoder.encode(user.getPassword())
        );

        userRepository.save(user);

        return new ResponseEntity<>("User Registered Successfully", HttpStatus.OK);
    }

    @PostMapping("/login")
    public  ResponseEntity<String> login(@RequestBody User user){
        Authentication authentication=
                authManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(),user.getPassword()));
        if(authentication.isAuthenticated())
            return new ResponseEntity<>(jwtService.generateToken(user.getUsername()),HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
}
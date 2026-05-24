package com.logicq1.crm.modules.auth.controller;

import com.logicq1.crm.modules.user.entity.User;
import com.logicq1.crm.modules.user.repository.UserRepository;
import com.logicq1.crm.modules.auth.service.AuthService;
import com.logicq1.crm.services.jwt.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private JwtUtil jwtService;

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

    @PostMapping("/login")
    public  String login(@RequestBody User user){
        Authentication authentication=
                authManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(),user.getPassword()));
        if(authentication.isAuthenticated())
            return jwtService.generateToken(user.getUsername());
        return "fail";
    }
}
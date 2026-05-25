package com.logicq1.crm.modules.auth.controller;

import com.logicq1.crm.modules.auth.enums.AuthProvider;
import com.logicq1.crm.modules.auth.enums.UserRole;
import com.logicq1.crm.modules.user.entity.User;
import com.logicq1.crm.modules.user.repository.UserRepository;
import com.logicq1.crm.modules.auth.service.AuthService;
import com.logicq1.crm.services.jwt.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

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
    public ResponseEntity<String> register(@RequestBody User user) {
        if(userRepository.findByEmail(user.getEmail()).isPresent()){
            return new ResponseEntity<>("email already exists",HttpStatus.BAD_REQUEST);
        }

        user.setPassword(
                passwordEncoder.encode(user.getPassword())
        );
        user.setCreatedAt(LocalDateTime.now());
        user.setRole(UserRole.MEMBER);
        user.setAuthBy(AuthProvider.LOCAL);
        user.setOnBoarded(false);

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
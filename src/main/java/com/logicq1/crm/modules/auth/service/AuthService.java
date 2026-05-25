package com.logicq1.crm.modules.auth.service;

import com.logicq1.crm.modules.auth.dto.RegisterRequest;
import com.logicq1.crm.modules.auth.enums.AuthProvider;
import com.logicq1.crm.modules.auth.enums.UserRole;
import com.logicq1.crm.modules.user.dto.UserDto;
import com.logicq1.crm.modules.user.entity.User;
import com.logicq1.crm.modules.user.mapper.UserMapper;
import com.logicq1.crm.modules.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public void registerUser(RegisterRequest data) throws RuntimeException{
        Optional<User> userExists = userRepository.findByEmail(data.getEmail());

        if(userExists.isPresent()){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }

        User user = userMapper.toUser(data);
        user.setPassword(passwordEncoder.encode(data.getPassword()));
        user.setOnBoarded(false);
        user.setAuthBy(AuthProvider.LOCAL);
        user.setRole(UserRole.MEMBER);
        userRepository.save(user);
    }

}

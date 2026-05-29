package com.logicq1.crm.modules.user.service;

import com.logicq1.crm.modules.user.dto.UserResponse;
import com.logicq1.crm.modules.user.entity.User;
import com.logicq1.crm.modules.user.mapper.UserMapper;
import com.logicq1.crm.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));

        return org.springframework.security.core.userdetails.User
                .builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles("USER")
                .build();
    }

    public UserResponse getUser(UUID id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        return userMapper.toResponse(user);
    }

    public Page<UserResponse> getUsers(Pageable pageable) {

        return userRepository.findAll(pageable)
                .map(userMapper::toResponse);
    }

    public UserResponse updateUser(UUID id, User updatedUser) {

        User existingUser = userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
        existingUser.setProfileImage(updatedUser.getProfileImage());
        existingUser.setJobTitle(updatedUser.getJobTitle());
        existingUser.setDepartment(updatedUser.getDepartment());

        User savedUser = userRepository.save(existingUser);

        return userMapper.toResponse(savedUser);
    }

    public void activateUser(UUID id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        user.setIsActive(true);

        userRepository.save(user);
    }

    public void deactivateUser(UUID id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        user.setIsActive(false);

        userRepository.save(user);
    }
}
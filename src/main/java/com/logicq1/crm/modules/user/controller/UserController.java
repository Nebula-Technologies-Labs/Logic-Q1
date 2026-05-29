package com.logicq1.crm.modules.user.controller;

import com.logicq1.crm.modules.user.dto.UserResponse;
import com.logicq1.crm.modules.user.entity.User;
import com.logicq1.crm.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(userService.getUser(id));
    }

    @GetMapping
    public ResponseEntity<Page<UserResponse>> getUsers(
            Pageable pageable
    ) {
        return ResponseEntity.ok(userService.getUsers(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable UUID id,
            @RequestBody User user
    ) {
        return ResponseEntity.ok(
                userService.updateUser(id, user)
        );
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activateUser(
            @PathVariable UUID id
    ) {
        userService.activateUser(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateUser(
            @PathVariable UUID id
    ) {
        userService.deactivateUser(id);
        return ResponseEntity.noContent().build();
    }
}
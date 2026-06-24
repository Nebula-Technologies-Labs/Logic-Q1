package com.logicq1.crm.modules.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private boolean status;

    private String message;

    private String token;

    private UserData user;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserData {

        private String id;

        private String email;

        private String firstName;

        private String lastName;

        private String profileImage;

        private String jobTitle;

        private String phoneNumber;

        private String department;

        private Boolean onBoarded;

        private Boolean emailVerified;

        private UUID orgId;

        private UUID roleId;
    }
}
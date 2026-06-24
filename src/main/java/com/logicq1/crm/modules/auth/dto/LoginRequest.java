package com.logicq1.crm.modules.auth.dto;

import lombok.Data;

@Data
public class LoginRequest {
    String email;
    String password;
}

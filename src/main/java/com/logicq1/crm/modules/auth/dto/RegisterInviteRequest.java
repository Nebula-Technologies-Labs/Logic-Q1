package com.logicq1.crm.modules.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterInviteRequest {

    private String token;

    private String firstName;

    private String lastName;

    private String password;
}
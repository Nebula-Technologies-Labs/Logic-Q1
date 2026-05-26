package com.logicq1.crm.modules.forgotPassword.controller;

import com.logicq1.crm.modules.forgotPassword.service.ForgotPasswordService;
import com.logicq1.crm.modules.forgotPassword.utils.ChangePassword;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/forgotPassword")
@RequiredArgsConstructor
public class ForgotPasswordController {

    private final ForgotPasswordService forgotPasswordService;

    @PostMapping("/verifyMail/{email}")
    public ResponseEntity<String> verifyMail(@PathVariable String email) {

        return forgotPasswordService.verifyMail(email);
    }

    @PostMapping("/verifyOTP/{email}/{otp}")
    public ResponseEntity<String> verifyOTP(
            @PathVariable String email,
            @PathVariable Integer otp
    ) {

        return forgotPasswordService.verifyOTP(email, otp);
    }

    @PostMapping("/changePassword/{email}")
    public ResponseEntity<String> changePassword(
            @RequestBody ChangePassword changePassword,
            @PathVariable String email
    ) {

        return forgotPasswordService.changePassword(changePassword, email);
    }
}
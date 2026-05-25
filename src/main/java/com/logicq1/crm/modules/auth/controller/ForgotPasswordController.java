package com.logicq1.crm.modules.auth.controller;

import com.logicq1.crm.modules.auth.dto.MailBody;
import com.logicq1.crm.modules.auth.entity.ForgotPassword;
import com.logicq1.crm.modules.auth.repositories.ForgotPasswordRepository;
import com.logicq1.crm.modules.auth.service.EmailService;
import com.logicq1.crm.modules.auth.utils.OTPGenerator;
import com.logicq1.crm.modules.user.entity.User;
import com.logicq1.crm.modules.user.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/auth/forgotPassword")
public class ForgotPasswordController {

    private final UserRepository userRepository;

    private final EmailService emailService;

    private final ForgotPasswordRepository forgotPasswordRepository;



    public ForgotPasswordController(UserRepository userRepository, EmailService emailService, ForgotPasswordRepository forgotPasswordRepository) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.forgotPasswordRepository = forgotPasswordRepository;
    }


    //Send mail for email verification
    @PostMapping("/verifyMail/{email}")
    public ResponseEntity<String> verifyMail(@PathVariable String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("Please Provide Valid Email"));

        Integer otp = OTPGenerator.otpGenerator();

        MailBody mailBody = MailBody.builder()
                .to(email)
                .text("This is your OTP for Forgot Password request : " + otp)
                .subject("OTP for Forget Password request")
                .build();
        ForgotPassword forgotPassword = ForgotPassword.builder()
                .otp(otp)
                .user(user)
                .expirationTime(new Date(System.currentTimeMillis() + 5 * 60 * 1000)) // OTP valid for 5 minutes
                .build();

        emailService.sendSimpleMessage(mailBody);

        forgotPasswordRepository.save(forgotPassword);

        return ResponseEntity.ok("OTP sent to your email. Please check your inbox and use the OTP to reset your password. OTP is valid for 5 minutes.");


    }



}

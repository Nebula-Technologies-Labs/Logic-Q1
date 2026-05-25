package com.logicq1.crm.modules.auth.controller;

import com.logicq1.crm.modules.auth.dto.MailBody;
import com.logicq1.crm.modules.auth.entity.ForgotPassword;
import com.logicq1.crm.modules.auth.repositories.ForgotPasswordRepository;
import com.logicq1.crm.modules.auth.service.EmailService;
import com.logicq1.crm.modules.auth.utils.ChangePassword;
import com.logicq1.crm.modules.auth.utils.OTPGenerator;
import com.logicq1.crm.modules.user.entity.User;
import com.logicq1.crm.modules.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;

@RestController
@RequestMapping("/forgotPassword")
public class ForgotPasswordController {

    private final UserRepository userRepository;

    private final EmailService emailService;

    private final ForgotPasswordRepository forgotPasswordRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    public ForgotPasswordController(UserRepository userRepository, EmailService emailService, ForgotPasswordRepository forgotPasswordRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.forgotPasswordRepository = forgotPasswordRepository;
        this.passwordEncoder = passwordEncoder;
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


    @PostMapping("/verifyOTP/{email}/{otp}")
    public ResponseEntity<String> verifyOTP(@PathVariable String email, @PathVariable Integer otp ){
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("Please Provide Valid Email"));

        ForgotPassword fp = forgotPasswordRepository.findByOtpAndUser(otp,user)
                .orElseThrow(()-> new RuntimeException("Invalid OTP or OTP has expired. Please try again."));

        if(fp.getExpirationTime().before(Date.from(Instant.now()))){
            forgotPasswordRepository.delete(fp);
            return ResponseEntity.badRequest().body("OTP has expired. Please request a new OTP.");
        }

        return ResponseEntity.ok("OTP verified successfully. You can now reset your password.");


    }

    @PostMapping("/changePassword/{email}")
    public ResponseEntity<String> changePassword(@RequestBody ChangePassword changePassword,
                                                 @PathVariable String email){

        if(!Objects.equals(changePassword.password(), changePassword.repeatPassword())){
            return new ResponseEntity<>("Please enter the password again", HttpStatus.EXPECTATION_FAILED);
        }

        String encodedPassword = passwordEncoder.encode(changePassword.password());

        userRepository.updatePassword(email,encodedPassword);

        return ResponseEntity.ok("Password changed successfully");



    }


}

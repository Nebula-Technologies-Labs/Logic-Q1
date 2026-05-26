package com.logicq1.crm.modules.forgotPassword.service;

import com.logicq1.crm.modules.email.dto.MailBody;
import com.logicq1.crm.modules.forgotPassword.entity.PasswordResetTokens;
import com.logicq1.crm.modules.forgotPassword.repositories.PasswordResetTokenRepository;
import com.logicq1.crm.modules.forgotPassword.utils.ChangePassword;
import com.logicq1.crm.utils.OTPGenerator;
import com.logicq1.crm.modules.email.service.EmailService;
import com.logicq1.crm.modules.user.entity.User;
import com.logicq1.crm.modules.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ForgotPasswordService {

    private final UserRepository userRepository;

    private final EmailService emailService;

    private final PasswordResetTokenRepository passwordResetTokenRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public ResponseEntity<String> verifyMail(String email){

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Please Provide Valid Email"
                        ));

        Integer otp = OTPGenerator.otpGenerator();

        MailBody mailBody = MailBody.builder()
                .to(email)
                .text("This is your OTP for Forgot Password request : " + otp)
                .subject("OTP for Forget Password request")
                .build();

        PasswordResetTokens prt = passwordResetTokenRepository
                .findByUser(user)
                .orElse(new PasswordResetTokens());

        prt.setUser(user);
        prt.setToken(otp);
        prt.setVerified(false);
        prt.setCreated_at(Instant.now());
        prt.setExpire_at(
                new Date(System.currentTimeMillis() + 5 * 60 * 1000)
        );

        passwordResetTokenRepository.save(prt);

        emailService.sendSimpleMessage(mailBody);

        return ResponseEntity.ok(
                "OTP sent to your email successfully"
        );
    }

    public ResponseEntity<String> verifyOTP(String email, Integer otp){

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Please Provide Valid Email"));

        PasswordResetTokens prt = passwordResetTokenRepository.findByOtpAndUser(otp,user)
                .orElseThrow(() -> new RuntimeException("Invalid OTP or OTP has expired. Please try again."));

        if(prt.getExpire_at().before(Date.from(Instant.now()))){
            passwordResetTokenRepository.delete(prt);
            return ResponseEntity.badRequest()
                    .body("OTP has expired. Please request a new OTP.");
        }

        prt.setVerified(true);

        passwordResetTokenRepository.save(prt);

        return ResponseEntity.ok(
                "OTP verified successfully. You can now reset your password."
        );
    }

    public ResponseEntity<String> changePassword(ChangePassword changePassword, String email){

        if(!Objects.equals(changePassword.password(), changePassword.repeatPassword())){
            return new ResponseEntity<>(
                    "Please enter the password again",
                    HttpStatus.EXPECTATION_FAILED
            );
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        PasswordResetTokens prt = passwordResetTokenRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException(
                        "OTP verification required before password reset"
                ));

        if(!prt.isVerified()){
            return new ResponseEntity<>(
                    "Please verify OTP first",
                    HttpStatus.UNAUTHORIZED
            );
        }

        if(prt.getExpire_at().before(Date.from(Instant.now()))){
            passwordResetTokenRepository.delete(prt);

            return new ResponseEntity<>(
                    "OTP expired. Please request a new OTP",
                    HttpStatus.UNAUTHORIZED
            );
        }

        String encodedPassword =
                passwordEncoder.encode(changePassword.password());

        userRepository.updatePassword(email, encodedPassword);

        passwordResetTokenRepository.delete(prt);

        return ResponseEntity.ok("Password changed successfully");
    }
}
package com.logicq1.crm.modules.auth.service;

import com.logicq1.crm.modules.auth.dto.MailBody;
import com.logicq1.crm.modules.auth.entity.password_reset_tokens;
import com.logicq1.crm.modules.auth.repositories.PasswordResetTokenRepository;
import com.logicq1.crm.modules.auth.utils.ChangePassword;
import com.logicq1.crm.modules.auth.utils.OTPGenerator;
import com.logicq1.crm.modules.user.entity.User;
import com.logicq1.crm.modules.user.repository.UserRepository;
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

    public ResponseEntity<String> verifyMail(String email){

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Please Provide Valid Email"));

        passwordResetTokenRepository.deleteByUser(user);

        Integer otp = OTPGenerator.otpGenerator();

        MailBody mailBody = MailBody.builder()
                .to(email)
                .text("This is your OTP for Forgot Password request : " + otp)
                .subject("OTP for Forget Password request")
                .build();

        password_reset_tokens prt = password_reset_tokens.builder()
                .token(otp)
                .user(user)
                .expire_at(new Date(System.currentTimeMillis() + 5 * 60 * 1000))
                .created_at(Instant.now())
                .build();

        emailService.sendSimpleMessage(mailBody);

        passwordResetTokenRepository.save(prt);

        return ResponseEntity.ok(
                "OTP sent to your email. Please check your inbox and use the OTP to reset your password. OTP is valid for 5 minutes."
        );
    }

    public ResponseEntity<String> verifyOTP(String email, Integer otp){

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Please Provide Valid Email"));

        password_reset_tokens prt = passwordResetTokenRepository.findByOtpAndUser(otp,user)
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

        password_reset_tokens prt = passwordResetTokenRepository.findByUser(user)
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
package com.ecampus.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ecampus.model.*;
import com.ecampus.repository.*;

@Service
public class ForgotPasswordService {

    @Autowired 
    private UserRepository userRepo;

    @Autowired 
    private JavaMailSender mailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Inner class to hold token data in memory
    private static class TokenData {
        String username;
        LocalDateTime expiry;
        TokenData(String username, LocalDateTime expiry) {
            this.username = username;
            this.expiry = expiry;
        }
    }

    // Storage: Token -> Data (Username + Expiry)
    private final Map<String, TokenData> tokenStorage = new ConcurrentHashMap<>();

    public String createResetToken(String username) {
        // Check if user exists first
        if (userRepo.findWithName(username) == null) return null;

        String token = UUID.randomUUID().toString();
        tokenStorage.put(token, new TokenData(username, LocalDateTime.now().plusMinutes(15)));
        return token;
    }

    public void sendEmail(String toEmail, String token) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Password Reset Request");
        message.setText("Click the link to reset your password (valid for 15 mins):\n" 
                      + "http://localhost:8080/forgot-password/reset?token=" + token);
        mailSender.send(message);
    }

    public String validateTokenAndGetUsername(String token) {
        TokenData data = tokenStorage.get(token);
        if (data != null && data.expiry.isAfter(LocalDateTime.now())) {
            return data.username;
        }
        tokenStorage.remove(token); // Cleanup expired or invalid
        return null;
    }

    public void resetPassword(String username, String newPassword, String token) {
        Users user = userRepo.findWithName(username).orElseThrow(() -> new RuntimeException("User not found"));;
        if (user != null) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepo.save(user);
            tokenStorage.remove(token); // One-time use: delete after success
        }
    }
}

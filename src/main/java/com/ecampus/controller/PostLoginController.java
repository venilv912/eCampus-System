package com.ecampus.controller;

import com.ecampus.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PostLoginController {

    @GetMapping("/post-login")
    public String postLogin(Authentication authentication) {

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        String role = userDetails.getAuthorities()
                .iterator()
                .next()
                .getAuthority();

        return switch (role) {
            case "ROLE_STUDENT" -> "redirect:/student/dashboard";
            case "ROLE_FACULTY" -> "redirect:/faculty/dashboard";
            case "ROLE_ADMIN"   -> "redirect:/admin/dashboard";
            default             -> "redirect:/login";
        };
    }
}

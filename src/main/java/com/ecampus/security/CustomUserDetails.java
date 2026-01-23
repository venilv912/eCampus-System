package com.ecampus.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.ecampus.model.Users;

public class CustomUserDetails implements UserDetails {

    private final Users user;

    public CustomUserDetails(Users user) {
        this.user = user;
    }

    public Users getUser() {
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        String role = user.getUrole0(); // authoritative source

        if (role == null) {
            return List.of();
        }

        role = role.toUpperCase();

        // Collapse business roles → security roles
        if (role.equals("EMPLOYEE") || role.equals("DEAN")) {
            role = "FACULTY";
        }

        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    public String getUsername() {
        return user.getUnivId(); // login identifier
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public boolean isEnabled() {
        return true;
//        return Boolean.TRUE.equals(user.getStatus());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}

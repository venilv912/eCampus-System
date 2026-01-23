package com.ecampus.service;

import com.ecampus.model.Users;
import com.ecampus.repository.UserRepository;
import com.ecampus.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String univId)
            throws UsernameNotFoundException {

        Users user = userRepository.findByUnividWithRoles(univId)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found: " + univId));

        return new CustomUserDetails(user);
    }
}

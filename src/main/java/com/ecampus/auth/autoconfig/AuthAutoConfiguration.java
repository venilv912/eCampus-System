package com.ecampus.auth.autoconfig;

import com.ecampus.auth.config.RoleSecurityProperties;
import com.ecampus.auth.service.DatabaseUserDetailsService;
import com.ecampus.auth.service.RoleAwareSuccessHandler;
import com.ecampus.auth.user.UserDetailsRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Added for @PreAuthorize support
@EnableConfigurationProperties(RoleSecurityProperties.class)
public class AuthAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @ConditionalOnMissingBean(UserDetailsService.class)
    public UserDetailsService userDetailsService(UserDetailsRepository repository) {
        return new DatabaseUserDetailsService(repository);
    }

    @Bean
    @ConditionalOnMissingBean(AuthenticationSuccessHandler.class)
    public AuthenticationSuccessHandler authenticationSuccessHandler(RoleSecurityProperties props) {
        return new RoleAwareSuccessHandler(props.getDefaultSuccessUrls());
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            AuthenticationSuccessHandler authenticationSuccessHandler,
            RoleSecurityProperties props) throws Exception {

        http.authorizeHttpRequests(auth -> {
            // Permit internal and error paths
            auth.requestMatchers("/","/forgot-password/**","/login", "/error", "/h2-console/**").permitAll();

            // Allow static resources
            auth.requestMatchers("/css/**", "/js/**", "/images/**").permitAll();

            // Map roles from properties
            props.getRoleRules().forEach((role, paths) -> {
                paths.forEach(path -> auth.requestMatchers(path).hasAuthority(role));
            });

            auth.anyRequest().authenticated();
        });

        // Use Default Login Page (No .loginPage() call)
        http.formLogin(form -> form
                .loginPage("/login")
                .successHandler(authenticationSuccessHandler)
                .permitAll()
        );

        http.logout(logout -> logout.permitAll());

        // H2 Console Support & CSRF
        http.csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"));
        http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }
}

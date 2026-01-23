package com.ecampus.config;

import com.ecampus.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authProvider(
            CustomUserDetailsService userDetailsService) {

        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/grades/uploadcsv",
                                "/admin/defineElectives/**")
                )

                .authorizeHttpRequests(auth -> auth

                        // ---------- DENY FIRST (important) ----------
                        .requestMatchers("/faculty/current-courses/**")
                        .hasRole("FACULTY")

                        .requestMatchers("/faculty/directGradeEntry/**")
                        .hasRole("FACULTY")

                        // ---------- STUDENT ----------
                        .requestMatchers("/student/**")
                        .hasRole("STUDENT")

                        // ---------- FACULTY ----------
                        .requestMatchers("/faculty/**")
                        .hasRole("FACULTY")

                        // ---------- ADMIN ----------
                        .requestMatchers("/admin/**")
                        .hasRole("ADMIN")

                        // ---------- AUTH / STATIC ----------
                        .requestMatchers("/login",
                                "/post-login",
                                "/auth/**",
                                "/css/**",
                                "/js/**",
                                "/images/**")
                        .permitAll()


                        .anyRequest()
                        .authenticated()
                )

                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/post-login", true)
                        .permitAll()
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )

                .sessionManagement(session -> session
                        .invalidSessionUrl("/login")
                        .maximumSessions(1)
                );

        return http.build();
    }
}

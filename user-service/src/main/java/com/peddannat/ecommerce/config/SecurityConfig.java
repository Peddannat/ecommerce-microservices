package com.peddannat.ecommerce.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf->csrf.disable())

                .authorizeHttpRequests(auth->auth

                        // Registration and login must remain public.

                        .requestMatchers("/api/users/register","/api/users/login").permitAll()

                        // All other endpoints require authentication
                        .anyRequest().authenticated())
                // JWT-based APIs should not use server-side sessions.
                .sessionManagement(session->session.
                        sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }



    @Bean
    public PasswordEncoder passwordEncoder(){
        // BCrypt is used to store passwords securely as hashes.
        return new BCryptPasswordEncoder();
    }


}

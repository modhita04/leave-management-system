package com.modi.leave_management_system.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity

public class SecurityConfig {
    @Autowired
    private JwtFilter jwtFilter;
    @Bean
    public SecurityFilterChain  securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        //public, no token needed
                        .requestMatchers("/api/auth/**").permitAll()
                        //employees and above
                        .requestMatchers(HttpMethod.GET,
                                "/api/balances/**").hasAnyRole("EMPLOYEE", "MANAGER", "ADMIN")
                        .requestMatchers(HttpMethod.POST,
                                "/api/leaves/apply").hasAnyRole("EMPLOYEE", "MANAGER", "ADMIN")
                        .requestMatchers(HttpMethod.POST,
                                "/api/leaves/*/cancel").hasAnyRole("EMPLOYEE", "MANAGER", "ADMIN")
                        .requestMatchers(HttpMethod.GET,
                                "/api/leaves/my/**").hasAnyRole("EMPLOYEE", "MANAGER", "ADMIN")
                        //managers and above
                        .requestMatchers(HttpMethod.POST,
                                "/api/leaves/*/approve").hasAnyRole("MANAGER", "ADMIN")
                        .requestMatchers(HttpMethod.POST,
                                "/api/leaves/*/reject").hasAnyRole("MANAGER", "ADMIN")
                        .requestMatchers(HttpMethod.GET,
                                "/api/leaves/pending/**").hasAnyRole("MANAGER", "ADMIN")
                        .requestMatchers(HttpMethod.GET,
                                "/api/leaves/team/**").hasAnyRole("MANAGER", "ADMIN")
                        //admin only
                        .requestMatchers("/api/employees/**").hasRole("ADMIN")

                        // everything else needs at least a valid token
                        .anyRequest().authenticated()
                )
                //plug in Jwt filter before Spring's auth filter
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    //password encoder- used in AuthService
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}

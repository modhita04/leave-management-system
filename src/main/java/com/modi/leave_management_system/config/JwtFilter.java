package com.modi.leave_management_system.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtil jwtUtil;
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain  filterChain) throws ServletException, IOException {
        //1. Read the authorization header
        String authHeader = httpServletRequest.getHeader("Authorization");
        //2.If no token, skip filter, let SecurityConfig decide
        // login endpoint is public, it passes through
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }
        //3.strip "Bearer " to get the raw token
        String token =  authHeader.substring(7);
        //4.valiate and extract the identity
        if(jwtUtil.validateToken(token)) {
            String email = jwtUtil.extractEmail(token);
            String role = jwtUtil.extractRole(token);
            Long empId = jwtUtil.extractEmpId(token);
            //5. Store identity in request attribute, controllers read empId from now on
            httpServletRequest.setAttribute("empId", empId);
            httpServletRequest.setAttribute("role", role);
            //6. tell spring security that request is authenticated and
            // ROLE_ prefix is required by spring security convention
            UsernamePasswordAuthenticationToken authentication =  new UsernamePasswordAuthenticationToken(email,null, List.of(new SimpleGrantedAuthority("ROLE_"+role)));
            SecurityContextHolder.getContext().setAuthentication(authentication);

        }
        //7. continue to next filter /controller
        filterChain.doFilter(httpServletRequest, httpServletResponse);

    }


}

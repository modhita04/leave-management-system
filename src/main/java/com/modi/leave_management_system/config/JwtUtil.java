package com.modi.leave_management_system.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String jwtSecret;
    @Value("${jwt.expiration}")
    private String jwtExpiration;
    //build signing key from secret string
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }
    //generate token called after successful login
    //embeds empId, role, email into payload
    public String generateToken(String email, String empId, String role){
        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .claim("empId", empId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+ jwtExpiration))
                .signWith(getSigningKey())
                .compact();
    }
    //validate token
    //returns true if signature is valid and token is not expired
    public boolean validateToken(String token){
        try{
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        }catch (Exception e){
            return false;
        }
    }
    //extract claims - payload we embedded
    private Claims extractAllClaims(String token){
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getPayload();
    }
    public String extractEmail(String token){
        return extractAllClaims(token).getSubject();
    }
    public String extractRole(String token){
        return extractAllClaims(token).get("role", String.class);
    }
    public Long extractEmpId(String token){
        return extractAllClaims(token).get("empId", Long.class);
    }

}

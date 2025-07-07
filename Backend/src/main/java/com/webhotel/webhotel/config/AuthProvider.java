package com.webhotel.webhotel.config;

import java.util.Base64;
import java.util.Collections;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.webhotel.webhotel.dto.UserDto;
import com.webhotel.webhotel.service.UserService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Component
public class AuthProvider {
    @Value("${backend.secret.key}")
    private String secretKey;

    @Value("${backend.secret.refresh}")
    private String refreshKey;

    private final UserService userService;

    public AuthProvider(UserService userService) {
        this.userService = userService;
    }

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createToken(String login) {
        Date now = new Date();
        Date duration = new Date(now.getTime() + 30000);
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());

        return Jwts.builder()
                .setSubject(login)
                .setIssuedAt(now)
                .setExpiration(duration)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Authentication validateToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        String login = claims.getSubject();
        UserDto user = userService.findByUsername(login);
        return new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());

    }

    public String createRefreshToken(String username) {
        Date now = new Date();
        Date duration = new Date(now.getTime() + 7 * 24 * 60 * 60 * 1000);
        SecretKey key = Keys.hmacShaKeyFor(refreshKey.getBytes());

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(duration)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Authentication validateRefreshToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(refreshKey.getBytes());
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        String login = claims.getSubject();
        UserDto user = userService.findByUsername(login);
        return new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
    }

}

package com.modsensoftware.library_service.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class JwtService {
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    public Collection<? extends GrantedAuthority> extractAuthorities(Claims claims) {
        List<?> authorities = claims.get("authorities", List.class);

        return authorities.stream()
                .map((authority) -> new SimpleGrantedAuthority((String) authority))
                .collect(Collectors.toList());
    }

    public String extractSubject(Claims tokenClaims){
        return tokenClaims.getSubject();
    }

    public Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
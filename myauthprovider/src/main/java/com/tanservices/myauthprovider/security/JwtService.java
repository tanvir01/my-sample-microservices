package com.tanservices.myauthprovider.security;

import com.tanservices.myauthprovider.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Service
@Slf4j
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;
    private Key signingKey;
    @Value("${jwt.expiration}")
    private int jwtExpirationMs;

    @PostConstruct
    public void init() {
        byte[] encodedSecret = Base64.getEncoder().encode(jwtSecret.getBytes(StandardCharsets.UTF_8));
        signingKey = Keys.hmacShaKeyFor(encodedSecret);
    }

    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getId().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS256, signingKey)
                .compact();
    }

    public Claims getAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(signingKey)
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> jws = Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token);

            Claims claims = jws.getBody();
            Date expiration = claims.getExpiration();
            if (expiration != null && expiration.before(new Date())) {
                log.info("JWT Token Expired");
                return false;
            }
            JwtContextHolder.setJwtToken(this, token);

            return true;
        } catch (Exception ex) {
            log.info("JWT Token Exception: " + ex.getMessage());
            return false;
        }
    }
}


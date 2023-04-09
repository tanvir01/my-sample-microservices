package com.tanservices.order.security;

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
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class JwtService {

//    private final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
//    private final long validityInMilliseconds = 3600000; // 1h


    @Value("${jwt.secret}")
    private String jwtSecret;

    private Key signingKey;

    @PostConstruct
    public void init() {
        byte[] encodedSecret = Base64.getEncoder().encode(jwtSecret.getBytes(StandardCharsets.UTF_8));
        signingKey = Keys.hmacShaKeyFor(encodedSecret);
    }

//    public String createToken(String username) {
//        Claims claims = Jwts.claims().setSubject(username);
//
//
//        Date now = new Date();
//        Date validity = new Date(now.getTime() + validityInMilliseconds);
//
//        return Jwts.builder()
//                .setClaims(claims)
//                .setIssuedAt(now)
//                .setExpiration(validity)
//                .signWith(SignatureAlgorithm.HS256, secretKey)
//                .compact();
//    }


//    public Claims getAllClaimsFromToken(String token) {
//        return Jwts.parser().setSigningKey(signingKey).parseClaimsJws(token).getBody();
//    }

    public Claims getAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(signingKey)
                .parseClaimsJws(token)
                .getBody();
    }

//    public boolean validateToken(String token) {
//        try {
//            Jwts.parser().setSigningKey(signingKey).parseClaimsJws(token);
//            return true;
//        } catch (Exception ex) {
//            log.info("JWT Token Exception: " + ex.getMessage());
//            return false;
//        }
//    }

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

            return true;
        } catch (Exception ex) {
            log.info("JWT Token Exception: " + ex.getMessage());
            return false;
        }
    }
}


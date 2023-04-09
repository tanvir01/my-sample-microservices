package com.tanservices.myauthprovider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

//    private final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
//    private final long validityInMilliseconds = 3600000; // 1h


    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpirationMs;

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

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("name", user.getName());
        claims.put("email", user.getEmail());
        claims.put("address", user.getAddress());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS256, signingKey)
                .compact();
    }

    public Claims getUserInfoFromToken(String token) {
        return Jwts.parser().setSigningKey(signingKey).parseClaimsJws(token).getBody();
    }

}


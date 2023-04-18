package com.tanservices.order.security;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtContextHolder {
    private static final ThreadLocal<String> JWT_TOKEN = new ThreadLocal<>();
    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();

    public static String getJwtToken() {
        return JWT_TOKEN.get();
    }

    public static void setJwtToken(JwtService jwtService, String jwtToken) {
        JWT_TOKEN.set(jwtToken);
        Claims claims = jwtService.getAllClaims(jwtToken);
        USER_ID.set(Long.parseLong(claims.getSubject()));
    }

    public static Long getUserId() {
        return USER_ID.get();
    }

    public static void clear() {
        JWT_TOKEN.remove();
        USER_ID.remove();
    }
}







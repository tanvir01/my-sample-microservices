package com.tanservices.notification;

import com.tanservices.notification.openfeign.MyauthproviderClient;
import com.tanservices.notification.openfeign.User;
import com.tanservices.notification.security.JwtContextHolder;
import com.tanservices.notification.security.JwtService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FetchUserInfoService {

    private final MyauthproviderClient myauthproviderClient;

    private final JwtService jwtService;

    public FetchUserInfoService(MyauthproviderClient myauthproviderClient, JwtService jwtService) {
        this.myauthproviderClient = myauthproviderClient;
        this.jwtService = jwtService;
    }

    public Optional<User> getUserInfo(String token) {
        if (!jwtService.validateToken(token)) {
            return null;
        }
        return Optional.of(myauthproviderClient.getUserById(JwtContextHolder.getUserId()));
    }
}

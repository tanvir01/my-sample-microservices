package com.tanservices.notification;

import com.tanservices.notification.openfeign.MyauthproviderClient;
import com.tanservices.notification.openfeign.User;
import org.springframework.stereotype.Service;

@Service
public class FetchUserInfoService {

    private final MyauthproviderClient myauthproviderClient;

    public FetchUserInfoService(MyauthproviderClient myauthproviderClient) {
        this.myauthproviderClient = myauthproviderClient;
    }

    public User getUserInfo(Long id) {
        return myauthproviderClient.getUserById(id);
    }
}

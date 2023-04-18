package com.tanservices.notification.openfeign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "myauthprovider", url = "${myauthprovider-service.url}", configuration = FeignConfiguration.class)
public interface MyauthproviderClient {

    @GetMapping("/user/{userId}")
    User getUserById(@PathVariable("userId") Long id);
}

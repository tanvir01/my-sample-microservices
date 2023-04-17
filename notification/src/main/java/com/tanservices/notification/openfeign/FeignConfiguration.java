package com.tanservices.notification.openfeign;

//import com.tanservices.order.security.JwtContextHolder;
//import feign.RequestInterceptor;
//import feign.RequestTemplate;
import feign.okhttp.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfiguration {

    @Bean
    public OkHttpClient client() {
        return new OkHttpClient();
    }


//    @Bean
//    public RequestInterceptor requestInterceptor() {
//        return new RequestInterceptor() {
//            @Override
//            public void apply(RequestTemplate template) {
//                String token = JwtContextHolder.getJwtToken();
//                if (token != null) {
//                    template.header("Authorization", "Bearer " + token);
//                }
//            }
//        };
//    }
}

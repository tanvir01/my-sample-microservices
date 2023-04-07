package com.tanservices.myauthprovider;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class MyauthproviderApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyauthproviderApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			userRepository.save(new User("user1", passwordEncoder.encode("password1")));
			userRepository.save(new User("user2", passwordEncoder.encode("password2")));
			userRepository.save(new User("user3", passwordEncoder.encode("password3")));
		};
	}
}

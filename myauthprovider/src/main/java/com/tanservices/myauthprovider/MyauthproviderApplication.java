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

			userRepository.save(User.builder().name("Sample User1").email("user1@gmail.com").address("Sample Str. 1, 10781, Berlin").username("user1").password(passwordEncoder.encode("password1")).build());
			userRepository.save(User.builder().name("Sample User2").email("user2@gmail.com").address("Sample Str. 2, 10782, Berlin").username("user2").password(passwordEncoder.encode("password2")).build());
			userRepository.save(User.builder().name("Sample User3").email("user3@gmail.com").address("Sample Str. 3, 10783, Berlin").username("user3").password(passwordEncoder.encode("password3")).build());

		};
	}
}

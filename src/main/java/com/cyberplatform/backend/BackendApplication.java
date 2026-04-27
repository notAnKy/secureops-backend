package com.cyberplatform.backend;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.cyberplatform.backend.repository.UserRepository;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync 
@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}
	
	@Bean
	CommandLineRunner test(UserRepository userRepository) {
		return args -> {
			System.out.println("Number of users: " + userRepository.count());
		};
	}

}

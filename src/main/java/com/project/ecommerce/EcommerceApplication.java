package com.project.ecommerce;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.project.ecommerce.model.User;
import com.project.ecommerce.repository.UserRepository;

@SpringBootApplication
public class EcommerceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EcommerceApplication.class, args);
	}
	
	@Bean
	CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
	    return args -> {
	        if (userRepository.findByUsername("nathaly").isEmpty()) {
	            User user = new User();
	            user.setUsername("nathaly");
	            user.setPassword(passwordEncoder.encode("12345")); // ¡Encriptar!
	            user.setRole("ADMIN");
	            userRepository.save(user);
	            System.out.println("Usuario inicial 'nathaly' creado.");
	        }
	    };
	}
}

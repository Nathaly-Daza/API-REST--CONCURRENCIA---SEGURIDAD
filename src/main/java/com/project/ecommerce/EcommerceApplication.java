package com.project.ecommerce;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.project.ecommerce.model.User;
import com.project.ecommerce.repository.UserRepository;

@SpringBootApplication
public class EcommerceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EcommerceApplication.class, args);
	}
	
	@Bean
	CommandLineRunner initDatabase(UserRepository userRepository) {
	    return args -> {
	        // No crearla dos veces
	        if (userRepository.findByUsername("nathaly").isEmpty()) {
	            User user = new User();
	            user.setUsername("nathaly");
	            user.setPassword("12345");
	            user.setRole("ADMIN");
	            userRepository.save(user);
	            System.out.println("Usuario inicial 'nathaly' creado.");
	        } else {
	            System.out.println("El usuario 'nathaly' ya existe, saltando creación.");
	        }
	    };
	}
}

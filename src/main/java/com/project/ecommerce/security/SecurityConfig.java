package com.project.ecommerce.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable()) // Desactivamos CSRF para APIs
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
            		.requestMatchers(
            			    "/", 
            			    "/index.html", 
            			    "/static/**", 
            			    "/auth/**",
            			    "/v3/api-docs/**",
            			    "/swagger-ui/**",
            			    "/swagger-ui.html"
            			).permitAll()
            		.requestMatchers(HttpMethod.GET, "/products/**").permitAll()
            		.requestMatchers(HttpMethod.POST, "/products/**").hasRole("ADMIN")
            		.requestMatchers(HttpMethod.PUT, "/products/**").hasRole("ADMIN")
            		.requestMatchers(HttpMethod.DELETE, "/products/**").hasRole("ADMIN")
            	    .requestMatchers("/orders/**").authenticated()
            	    .anyRequest().authenticated()
            	)
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }
    
 // Agrega este método en la clase:
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

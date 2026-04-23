package com.project.ecommerce.controller;

import com.project.ecommerce.dto.LoginRequest;
import com.project.ecommerce.model.User;
import com.project.ecommerce.repository.UserRepository;
import com.project.ecommerce.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    // Endpoint para obtener el token
    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginRequest request) {

        String username = request.username();
        String password = request.password();

        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Usuario no existe"));

        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

        return Map.of("token", token);
    }
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public String register(@RequestBody User user) {
        user.setRole("CLIENTE"); // Rol por defecto
        userRepository.save(user);
        return "Usuario registrado exitosamente";
    }

}

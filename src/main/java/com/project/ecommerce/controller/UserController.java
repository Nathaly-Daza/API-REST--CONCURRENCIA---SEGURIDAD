package com.project.ecommerce.controller;


import com.project.ecommerce.model.User;
import com.project.ecommerce.repository.UserRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository repo;

    public UserController(UserRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<User> getAll() {
        return repo.findAll();
    }

    @PutMapping("/{id}")
    public User update(@PathVariable Long id, @RequestBody User u) {
        User user = repo.findById(id).orElseThrow();
        user.setUsername(u.getUsername());
        user.setPassword(u.getPassword());
        return repo.save(user);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        User user = repo.findById(id).orElseThrow();
        user.setActive(false);
        repo.save(user);
        return "Usuario desactivado";
    }
}
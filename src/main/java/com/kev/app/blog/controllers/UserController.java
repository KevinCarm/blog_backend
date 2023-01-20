package com.kev.app.blog.controllers;

import com.kev.app.blog.entities.models.User;
import com.kev.app.blog.entities.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
public class UserController {
    @Autowired
    private IUserService service;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        Optional<User> user = Optional.ofNullable(service.getById(id));
        Map<String, Object> map;
        if (user.isEmpty()) {
            map = new HashMap<>();
            map.put("message", "User not found");
            map.put("status", HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map);
        }
        return ResponseEntity.ok(user.get());
    }

    @PostMapping("/users")
    public ResponseEntity<?> save(@RequestBody User user) {
        Optional<User> existing = Optional.ofNullable(service.getByEmail(user.getEmail()));
        Map<String, Object> map;
        if (existing.isPresent()) {
            map = new HashMap<>();
            map.put("message", String.format("Email %s already exists", user.getEmail()));
            map.put("status", HttpStatus.BAD_REQUEST);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = service.save(user);
        User response = new User();
        response.setEmail(savedUser.getEmail());
        response.setUsername(response.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
}

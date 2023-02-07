package com.kev.app.blog.controllers;

import com.kev.app.blog.entities.models.LoginRequest;
import com.kev.app.blog.entities.models.User;
import com.kev.app.blog.entities.services.IUserService;
import com.kev.app.blog.util.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
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
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private JWTUtil jwtUtil;

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
        user.setDescription("");
        User savedUser = service.save(user);
        User response = new User();
        response.setEmail(savedUser.getEmail());
        response.setUsername(savedUser.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Map<String, Object> map;
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );
        } catch (Exception e) {
            map = new HashMap<>();
            map.put("message", "Bad credentials");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(map);
        }
        final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());
        String jwt = jwtUtil.generateToken(userDetails);
        map = new HashMap<>();
        map.put("token", jwt);
        return ResponseEntity.ok(map);
    }
}

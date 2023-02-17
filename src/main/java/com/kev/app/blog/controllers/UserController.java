package com.kev.app.blog.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kev.app.blog.entities.models.LoginRequest;
import com.kev.app.blog.entities.models.User;
import com.kev.app.blog.entities.services.IUserService;
import com.kev.app.blog.util.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    private final String UPLOADS_PATH = "src/main/resources/uploads";

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

    @PostMapping(value = "/users", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> save(@RequestParam(required = false) MultipartFile file, @RequestParam String user) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        User objUser = mapper.readValue(user, User.class);

        Optional<User> existing = Optional.ofNullable(service.getByEmail(objUser.getEmail()));
        Map<String, Object> map;
        if (existing.isPresent()) {
            map = new HashMap<>();
            map.put("message", String.format("Email %s already exists", objUser.getEmail()));
            map.put("status", HttpStatus.BAD_REQUEST);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
        }
        objUser.setPassword(passwordEncoder.encode(objUser.getPassword()));
        objUser.setDescription("");

        if (file != null) {
            try {
                byte[] bytes = file.getBytes();
                Path path = Paths.get(UPLOADS_PATH, file.getOriginalFilename());
                Files.write(path, bytes);
                objUser.setImagePath(file.getOriginalFilename());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        User savedUser = service.save(objUser);
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

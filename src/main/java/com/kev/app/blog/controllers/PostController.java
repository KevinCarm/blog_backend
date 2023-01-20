package com.kev.app.blog.controllers;

import com.kev.app.blog.entities.models.Post;
import com.kev.app.blog.entities.services.IPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
public class PostController {
    @Autowired
    private IPostService service;

    @PostMapping("/post")
    public ResponseEntity<?> save(@RequestBody Post post) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(post));
    }

    //TODO: get posts with pagination

    @GetMapping("/post/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        Optional<Post> post = Optional.ofNullable(service.getById(id));
        Map<String, Object> map;
        if (post.isEmpty()) {
            map = new HashMap<>();
            map.put("message", "Post not found");
            map.put("status", HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map);
        }
        return ResponseEntity.ok(post.get());
    }
}

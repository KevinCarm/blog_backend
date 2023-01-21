package com.kev.app.blog.controllers;

import com.kev.app.blog.entities.models.Post;
import com.kev.app.blog.entities.models.User;
import com.kev.app.blog.entities.services.IPostService;
import com.kev.app.blog.entities.services.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin("*")

public class PostController {
    private final Logger log = LoggerFactory.getLogger(PostController.class);
    @Autowired
    private IPostService service;
    @Autowired
    private IUserService userService;
    private final String UPLOADS_PATH = "src/main/resources/uploads";

    @PostMapping(value = "/post", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> save(@RequestParam MultipartFile file, @RequestParam String content) {
        Post post = new Post();
        post.setContent(content);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOADS_PATH, file.getOriginalFilename());
            Files.write(path, bytes);
            post.setImagePath(file.getOriginalFilename());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        User user = userService.getByEmail(currentPrincipalName);
        post.setPostDate(new Date());
        user.getPosts().add(post);
        post.setUser(user);
        service.save(post);
        return ResponseEntity.status(HttpStatus.CREATED).body(post);
    }

   /* @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFile(@RequestParam MultipartFile file, @RequestParam String content) {
        log.info(file.getOriginalFilename());
        log.info(content);
        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOADS_PATH, file.getOriginalFilename());
            Files.write(path, bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok().build();
    }*/


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
        Post response = post.get();
        response.getUser().setPassword("");
        response.getUser().setEmail("");
        return ResponseEntity.ok(post.get());
    }

    @GetMapping("/file/{file}")
    public ResponseEntity<?> getFile(@PathVariable("file") String fileName) {
        try {
            Path path = Paths.get(UPLOADS_PATH, fileName);
            File file = new File(path.toUri());
            MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
            if (file.toString().endsWith(".png")) {
                mediaType = MediaType.IMAGE_PNG;
            } else if (file.toString().endsWith(".jpeg") || file.toString().endsWith(".jpg")) {
                mediaType = MediaType.IMAGE_JPEG;
            }
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                    .header(HttpHeaders.CONTENT_TYPE, "image")
                    .contentType(mediaType)
                    .contentLength(file.length())
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok("File");
    }
}

package com.kev.app.blog.entities.services;

import com.kev.app.blog.entities.models.LoginRequest;
import com.kev.app.blog.entities.models.User;

public interface IUserService {
    User getById(Long id);
    User getByEmail(String email);
    User save(User user);
    User login(LoginRequest loginRequest);

    //TODO: Get all user's posts
}

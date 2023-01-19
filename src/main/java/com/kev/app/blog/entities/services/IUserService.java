package com.kev.app.blog.entities.services;

import com.kev.app.blog.entities.models.User;

public interface IUserService {
    User getById(Long id);
    User getByEmail(String email);

    //TODO: Get all user's posts
}

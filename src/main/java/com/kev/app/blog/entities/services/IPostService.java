package com.kev.app.blog.entities.services;

import com.kev.app.blog.entities.models.Post;

import java.util.List;

public interface IPostService {
    Post getById(Long id);
    //TODO: get by user id
    //TODO: get comments
    List<Post> getPosts();
    Post save(Post post);
    void updateNumberOfFavorites(Long id, Boolean isToIncrease);
}

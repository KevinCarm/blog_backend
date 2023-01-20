package com.kev.app.blog.entities.services;

import com.kev.app.blog.entities.models.Post;
import com.kev.app.blog.entities.repositories.IPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IPostServiceImpl implements IPostService{
    @Autowired
    private IPostRepository repository;

    @Override
    public Post getById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public List<Post> getPosts() {
        return repository.findAll();
    }

    @Override
    public Post save(Post post) {
        return repository.save(post);
    }
}

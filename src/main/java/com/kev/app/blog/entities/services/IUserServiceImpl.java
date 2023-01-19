package com.kev.app.blog.entities.services;

import com.kev.app.blog.entities.models.User;
import com.kev.app.blog.entities.repositories.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IUserServiceImpl implements IUserService{

    @Autowired
    private IUserRepository repository;

    @Override
    public User getById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public User getByEmail(String email) {
        return repository.getByEmail(email);
    }
}

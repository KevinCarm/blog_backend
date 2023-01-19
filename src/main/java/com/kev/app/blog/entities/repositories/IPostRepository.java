package com.kev.app.blog.entities.repositories;

import com.kev.app.blog.entities.models.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPostRepository extends JpaRepository<Post, Long> {
}

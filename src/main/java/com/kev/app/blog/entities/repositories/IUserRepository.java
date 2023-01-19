package com.kev.app.blog.entities.repositories;

import com.kev.app.blog.entities.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserRepository extends JpaRepository<User, Long> {
    @Query(value = "SELECT u FROM User WHERE u.email = :email")
    User getByEmail(@Param("email") String email);
}

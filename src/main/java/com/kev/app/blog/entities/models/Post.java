package com.kev.app.blog.entities.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;
    @Column(name = "post_date")
    private Date postDate;
    @Column(name = "image_path")
    private String imagePath;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties("posts")
    private User user;

    @Column(name = "number_favorites")
    private Integer numberFavorites;

    public Integer getNumberFavorites() {
        return numberFavorites;
    }

    public void setNumberFavorites(Integer numberFavorites) {
        this.numberFavorites = numberFavorites;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getPostDate() {
        return postDate;
    }

    public void setPostDate(Date postDate) {
        this.postDate = postDate;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", postDate=" + postDate +
                ", imagePath='" + imagePath + '\'' +
                ", user=" + user +
                '}';
    }
}

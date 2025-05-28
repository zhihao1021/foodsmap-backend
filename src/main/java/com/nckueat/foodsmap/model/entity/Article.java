package com.nckueat.foodsmap.model.entity;

import lombok.NonNull;
import org.springframework.data.annotation.Id;
import lombok.Data;

@Data
public class Article {
    @Id
    private Long id;

    @NonNull
    private String title;

    @NonNull
    private String context;

    @NonNull
    private Long like;
    
    @NonNull
    private String[] tags;
    
    @NonNull
    private Long authorID;

    @NonNull
    private String[] mediaURL;

    public Article(Long id, String title, String context, Long like, String[] tags, Long authorID) {
        this.id = id;
        this.title = title;
        this.context = context;
        this.like = like;
        this.tags = tags;
        this.authorID = authorID;
    }
}


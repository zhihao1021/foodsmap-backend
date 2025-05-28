package com.nckueat.foodsmap.model.dto.request;

import lombok.NonNull;

import java.util.Optional;

import lombok.Data;

@Data
public class ArticleCreate {
    @NonNull
    private String title;

    @NonNull
    private String context;

    @NonNull
    private Long like;
    
    @NonNull
    private String[] tags;

    @NonNull
    private Optional<String[]> mediaURL;
    
}

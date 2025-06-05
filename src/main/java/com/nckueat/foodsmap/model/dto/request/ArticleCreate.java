package com.nckueat.foodsmap.model.dto.request;

import lombok.NonNull;

import lombok.Data;

@Data
public class ArticleCreate {
    @NonNull
    private final String title;

    @NonNull
    private final String context;

    @NonNull
    private final String[] mediaURL;
}

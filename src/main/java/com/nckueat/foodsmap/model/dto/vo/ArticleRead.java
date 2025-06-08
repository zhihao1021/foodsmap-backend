package com.nckueat.foodsmap.model.dto.vo;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class ArticleRead {
    @NonNull
    private final Long id;

    @NonNull
    private final String title;

    @NonNull
    private final String context;
    
    @NonNull
    private final Long like;

    @NonNull
    private final Long date;
    
    @NonNull
    private final String[] tags;
    
    @NonNull
    private final Long authorID;

    @NonNull
    private final String[] mediaURL;

    @NonNull
    private final String googleMapURL;
}

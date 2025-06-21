package com.nckueat.foodsmap.model.dto.vo;

import java.util.List;
import java.util.Set;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class ArticleRead {
    @NonNull
    private final String id;

    @NonNull
    private final String title;

    @NonNull
    private final String context;

    @NonNull
    private final Long createTime;

    @NonNull
    private final Long editTime;

    @NonNull
    private final Set<String> tags;

    @NonNull
    private final GlobalUserView author;

    @NonNull
    private final List<String> mediaList;

    @NonNull
    private final String googleMapUrl;

    @NonNull
    private final Long likesCount;

    private final boolean likedByUser;
}

package com.nckueat.foodsmap.model.dto.request;

import lombok.NonNull;
import lombok.Data;

@Data
public class ArticleSearch {
    @NonNull
    private final String[] searchContext;
}

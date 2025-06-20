package com.nckueat.foodsmap.model.dto.request;

import java.util.Optional;
import lombok.Data;

@Data
public class ArticleUpdate {
    private final Optional<String> title;
    private final Optional<String> context;
    private final Optional<String> googleMapUrl;
}

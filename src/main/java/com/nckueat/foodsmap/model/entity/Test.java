package com.nckueat.foodsmap.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import lombok.NonNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Document(indexName = "foodmap-test2", createIndex = true)
public class Test {
    @Id
    private Long id;

    @NonNull
    private String name;

    @NonNull
    private String description;
}

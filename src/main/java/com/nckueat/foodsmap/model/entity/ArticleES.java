package com.nckueat.foodsmap.model.entity;

import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@org.springframework.data.elasticsearch.annotations.Document(indexName = "foodsmap-articles")
public class ArticleES {
    @Id
    @ReadOnlyProperty
    private Long id;

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String title;

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String context;

    @Field(type = FieldType.Keyword)
    private String[] tags;

    public static ArticleES fromArticle(@NonNull Article article) {
        return ArticleES.builder().id(article.getId()).title(article.getTitle())
                .context(article.getContext()).tags(article.getTags().toArray(new String[0]))
                .build();
    }
}


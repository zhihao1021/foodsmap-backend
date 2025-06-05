package com.nckueat.foodsmap.model.entity;

import lombok.NonNull;
import org.springframework.data.annotation.Id;
import com.nckueat.foodsmap.model.dto.request.ArticleCreate;
import com.nckueat.foodsmap.model.dto.vo.ArticleRead;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
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
    @Builder.Default
    private String[] mediaURL = new String[0];

    public static Article fromArticleCreate(Long id, Long authorId, ArticleCreate articleCreate, 
                                            String[] tags) {
        return Article.builder().id(id).title(articleCreate.getTitle())
                .context(articleCreate.getContext()).like(0L).tags(tags)
                .authorID(authorId).mediaURL(articleCreate.getMediaURL()).build();
    }

    public static ArticleRead toArticleRead(Article article) {
        return ArticleRead.builder().id(article.getId()).title(article.getTitle())
                .context(article.getContext()).like(0L).tags(article.getTags())
                .authorID(article.getAuthorID()).mediaURL(article.getMediaURL()).build();
    }
}


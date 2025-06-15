package com.nckueat.foodsmap.model.entity;

import lombok.NonNull;
import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import com.nckueat.foodsmap.Utils.TagsSpliter;
import com.nckueat.foodsmap.model.dto.request.ArticleCreate;
import com.nckueat.foodsmap.model.dto.request.ArticleUpdate;
import com.nckueat.foodsmap.model.dto.vo.ArticleRead;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@Table(name = "articles")
@AllArgsConstructor
@NoArgsConstructor
public class Article {
    @Id
    private Long id;

    @NonNull
    private String title;

    @NonNull
    private String context;

    @NonNull
    private Long likes;

    // @NonNull
    // @DBRef
    // @Builder.Default
    // private User[] likeUsers = new User[0];

    @NonNull
    private Long date;

    @NonNull
    private Set<String> tags;

    @NonNull
    private Long authorId;

    @NonNull
    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "article_media_urls", joinColumns = @JoinColumn(name = "article_id"))
    @Column(name = "media_url", columnDefinition = "TEXT")
    @OrderColumn(name = "index")
    private String[] mediaUrl = new String[0];

    @NonNull
    @Builder.Default
    private String googleMapUrl = "";

    public static Article fromArticleCreate(Long id, Long authorId, ArticleCreate articleCreate) {
        Set<String> tagsSet = new HashSet<String>(TagsSpliter.spilt(articleCreate.getContext()));

        return Article.builder().id(id).title(articleCreate.getTitle())
                .context(articleCreate.getContext()).likes(0L).date(System.currentTimeMillis())
                .tags(tagsSet).authorId(authorId).mediaUrl(articleCreate.getMediaUrl()).build();
    }

    public ArticleRead toArticleRead() {
        return ArticleRead.builder().id(id.toString()).title(title).context(context).likes(likes)
                .date(date).tags(tags.toArray(new String[0])).authorID(authorId).mediaURL(mediaUrl)
                .googleMapURL(googleMapUrl).build();
    }

    public void update(ArticleUpdate data) {
        data.getTitle().ifPresent(this::setTitle);
        data.getContext().ifPresent(v -> {
            this.setContext(v);

            this.tags = new HashSet<String>(TagsSpliter.spilt(v));
        });
        data.getMediaURL().ifPresent(this::setMediaUrl);
        data.getGoogleMapUrl().ifPresent(this::setGoogleMapUrl);
    }
}


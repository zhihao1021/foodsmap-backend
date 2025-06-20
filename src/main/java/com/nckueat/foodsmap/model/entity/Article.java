package com.nckueat.foodsmap.model.entity;

import lombok.NonNull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.Formula;

import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import com.nckueat.foodsmap.Utils.TagsSpliter;
import com.nckueat.foodsmap.model.dto.request.ArticleCreate;
import com.nckueat.foodsmap.model.dto.request.ArticleUpdate;
import com.nckueat.foodsmap.model.dto.vo.ArticleRead;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
    @Column(columnDefinition = "TEXT")
    private String title;

    @NonNull
    @Column(columnDefinition = "TEXT")
    private String context;

    @NonNull
    private Long createTime;

    @NonNull
    private Long editTime;

    @NonNull
    @Column(columnDefinition = "TEXT[]")
    private Set<String> tags;

    @NonNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @NonNull
    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "article_media_urls", joinColumns = @JoinColumn(name = "article_id"))
    @Column(nullable = false, name = "media_url", columnDefinition = "TEXT")
    @OrderColumn(name = "index")
    private List<String> mediaList = new ArrayList<>();

    @NonNull
    @Builder.Default
    @Column(columnDefinition = "TEXT")
    private String googleMapUrl = "";

    @NonNull
    @Builder.Default
    @ManyToMany(mappedBy = "likedArticles", cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            fetch = FetchType.LAZY)
    private Set<User> likesUsers = new HashSet<>();

    @Builder.Default
    @Formula("(SELECT COUNT(*) FROM user_likes ul WHERE ul.article_id = id)")
    private Long likesCount = 0L;

    public static Article fromArticleCreate(Long id, User user, ArticleCreate articleCreate) {
        Set<String> tagsSet = new HashSet<String>(TagsSpliter.spilt(articleCreate.getContext()));

        return Article.builder().id(id).title(articleCreate.getTitle())
                .context(articleCreate.getContext()).createTime(System.currentTimeMillis())
                .editTime(System.currentTimeMillis()).tags(tagsSet).author(user).build();
    }

    public ArticleRead toArticleRead() {
        return ArticleRead.builder().id(id.toString()).title(title).context(context)
                .createTime(createTime).editTime(editTime).tags(tags)
                .author(author.toGlobalUserView()).mediaList(mediaList).googleMapUrl(googleMapUrl)
                .likesCount(likesCount).build();
    }

    public void update(ArticleUpdate data) {
        data.getTitle().ifPresent(this::setTitle);
        data.getContext().ifPresent(v -> {
            this.setContext(v);

            this.tags = new HashSet<String>(TagsSpliter.spilt(v));
        });
        data.getGoogleMapUrl().ifPresent(this::setGoogleMapUrl);

        this.setEditTime(System.currentTimeMillis());
    }

    public void appendMedia(Long mediaId) {
        this.appendMedia(mediaId.toString());
    }

    public void appendMedia(String mediaId) {
        this.mediaList.add(mediaId);
    }
}

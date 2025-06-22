package com.nckueat.foodsmap.controller;

import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.yaml.snakeyaml.util.Tuple;
import org.springframework.beans.factory.annotation.Autowired;

import com.nckueat.foodsmap.model.entity.User;
import com.nckueat.foodsmap.annotation.CurrentUser;
import com.nckueat.foodsmap.annotation.CurrentUserId;
import com.nckueat.foodsmap.annotation.OptionalCurrentUserId;
import com.nckueat.foodsmap.component.nextId.NextIdTokenConverter;
import com.nckueat.foodsmap.model.entity.Article;
import com.nckueat.foodsmap.service.ArticleService;
import com.nckueat.foodsmap.model.dto.request.ArticleCreate;
import com.nckueat.foodsmap.model.dto.request.ArticleUpdate;
import com.nckueat.foodsmap.model.dto.response.ListResponse;
import com.nckueat.foodsmap.model.dto.vo.ArticleRead;

@RestController
@RequestMapping("/article")
public class ArticlesController {

    @Autowired
    private ArticleService articlesService;
    @Autowired
    private NextIdTokenConverter nextIdTokenConverter;

    @PostMapping("")
    public ResponseEntity<ArticleRead> createArticle(@RequestBody ArticleCreate data,
            @CurrentUser User user) {
        Article article = articlesService.createArticle(user, data);

        URI location = URI.create(String.format("/article/by-id/%s", article.getId().toString()));
        return ResponseEntity.created(location).body(article.toArticleRead());
    }

    @GetMapping("by-id/{articleId}")
    public ResponseEntity<ArticleRead> getArticle(@NonNull @PathVariable Long articleId,
            @OptionalCurrentUserId Long searcherId) {
        Article article = articlesService.findArticleById(articleId);
        boolean userLike = articlesService.isUserLikeArticle(searcherId, articleId);

        return ResponseEntity.ok().body(article.toArticleRead(userLike));
    }

    @PutMapping("by-id/{articleId}")
    public ResponseEntity<ArticleRead> updateArticle(@RequestBody ArticleUpdate data,
            @NonNull @PathVariable Long articleId, @CurrentUserId Long userId) {
        Article article = articlesService.updateArticle(articleId, userId, data);
        boolean userLike = articlesService.isUserLikeArticle(userId, articleId);

        URI location = URI.create(String.format("/article/by-id/%s", articleId));

        return ResponseEntity.created(location).body(article.toArticleRead(userLike));
    }

    @PutMapping("by-id/{articleId}/files")
    public ResponseEntity<ArticleRead> appendArticleMedias(@NonNull @PathVariable Long articleId,
            @CurrentUserId Long userId,
            @RequestParam(name = "files") List<MultipartFile> mediaList) {
        Article article = articlesService.appendArticleMedia(articleId, userId, mediaList);
        boolean userLike = articlesService.isUserLikeArticle(userId, articleId);

        URI location = URI.create(String.format("/article/by-id/%s", articleId));

        return ResponseEntity.created(location).body(article.toArticleRead(userLike));
    }

    @PutMapping("by-id/{articleId}/like")
    public ResponseEntity<Void> likeArticle(@NonNull @PathVariable Long articleId,
            @CurrentUser User user) {
        articlesService.likeArticle(articleId, user);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("by-id/{articleId}/like")
    public ResponseEntity<Void> dislikeArticle(@NonNull @PathVariable Long articleId,
            @CurrentUser User user) {
        articlesService.dislike(articleId, user);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("by-id/{articleId}")
    public ResponseEntity<Void> deleteArticle(@NonNull @PathVariable Long articleId,
            @CurrentUserId Long userId) {
        articlesService.deleteArticle(articleId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("by-tag/{tagName}")
    public ResponseEntity<ListResponse<ArticleRead>> getArticlesByTag(
            @NonNull @PathVariable String tagName, @RequestParam(defaultValue = "100") int limit,
            @RequestParam(required = false) String token, @OptionalCurrentUserId Long searcherId) {

        Tuple<List<Article>, String> resultPair =
                articlesService.getArticlesByTag(tagName, limit, token);
        List<Article> articles = resultPair._1();
        String searchAfterTag = resultPair._2();

        String newToken =
                articles.size() < limit ? null : nextIdTokenConverter.getNextToken(searchAfterTag);

        List<Long> userLikeIds = articlesService.getUserLikeArticleIds(searcherId, articles);

        return ResponseEntity.ok(new ListResponse<>(
                articles.stream().map(Article.toArticleReadFunction(userLikeIds)).toList(),
                newToken));
    }

    @GetMapping("by-context/{context}")
    public ResponseEntity<ListResponse<ArticleRead>> getArticlesByContext(
            @NonNull @PathVariable String context, @RequestParam(defaultValue = "100") int limit,
            @RequestParam(required = false) String token) {

        Tuple<List<Article>, String> resultPair =
                articlesService.getArticlesByContext(context, limit, token);
        List<Article> articles = resultPair._1();
        String searchAfterTag = resultPair._2();

        String newToken =
                articles.size() < limit ? null : nextIdTokenConverter.getNextToken(searchAfterTag);

        return ResponseEntity.ok(new ListResponse<>(
                articles.stream().map(Article::toArticleRead).toList(), newToken));
    }

    @GetMapping("latest")
    public ResponseEntity<ListResponse<ArticleRead>> getLatestArticles(
            @RequestParam(defaultValue = "10000") int limit,
            @OptionalCurrentUserId Long searcherId) {
        List<Article> articles = articlesService.getLatestArticles(limit);
        List<Long> userLikeIds = articlesService.getUserLikeArticleIds(searcherId, articles);

        return ResponseEntity.ok(new ListResponse<>(
                articles.stream().map(Article.toArticleReadFunction(userLikeIds)).toList(), null));
    }
}

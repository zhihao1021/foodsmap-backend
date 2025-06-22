package com.nckueat.foodsmap.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.yaml.snakeyaml.util.Tuple;
import com.nckueat.foodsmap.component.mediaManager.MediaManager;
import com.nckueat.foodsmap.component.nextId.NextIdTokenConverter;
import com.nckueat.foodsmap.component.snowflakeId.SnowflakeIdGenerator;
import com.nckueat.foodsmap.exception.ArticleNotFound;
import com.nckueat.foodsmap.exception.UserNotFound;
import com.nckueat.foodsmap.model.entity.Article;
import com.nckueat.foodsmap.model.entity.ArticleES;
import com.nckueat.foodsmap.model.entity.User;
import com.nckueat.foodsmap.repository.elasticsearch.ArticleESRepository;
import com.nckueat.foodsmap.repository.postgresql.ArticleRepository;
import com.nckueat.foodsmap.repository.postgresql.UserRepository;
import jakarta.transaction.Transactional;
import com.nckueat.foodsmap.model.dto.request.ArticleCreate;
import com.nckueat.foodsmap.model.dto.request.ArticleUpdate;
import com.nckueat.foodsmap.model.elasticesarch.SearchAfterPage;
import java.util.List;

@Service
public class ArticleService {

    @Autowired
    private SnowflakeIdGenerator snowflakeIdGenerator;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private ArticleESRepository articleESRepository;
    @Autowired
    private NextIdTokenConverter nextIdTokenConverter;
    @Autowired
    private MediaManager mediaManager;

    public Article createArticle(@NonNull User user, @NonNull ArticleCreate articlesCreate) {
        Long articleId = snowflakeIdGenerator.nextId();

        Article article = Article.fromArticleCreate(articleId, user, articlesCreate);
        article = articleRepository.save(article);
        articleESRepository.save(ArticleES.fromArticle(article));

        return article;
    }

    public Article appendArticleMedia(@NonNull Long articleId, @NonNull Long userId,
            @NonNull List<MultipartFile> mediaList) throws ArticleNotFound {
        Article article = articleRepository.findByIdAndAuthorId(articleId, userId)
                .orElseThrow(() -> new ArticleNotFound(articleId));

        mediaList.stream().forEach(media -> {
            Long mediaId = mediaManager.saveArticleMedia(articleId, media);
            article.appendMedia(mediaId);
        });

        articleRepository.save(article);

        return article;
    }

    public Article findArticleById(@NonNull Long articleId) throws ArticleNotFound {
        return articleRepository.findById(articleId)
                .orElseThrow(() -> new ArticleNotFound(articleId));
    }

    public Article updateArticle(@NonNull Long articleId, @NonNull Long userId, ArticleUpdate data)
            throws ArticleNotFound {
        Article article = articleRepository.findByIdAndAuthorId(articleId, userId)
                .orElseThrow(() -> new ArticleNotFound());

        article.update(data);
        articleRepository.save(article);
        articleESRepository.save(ArticleES.fromArticle(article));

        return article;
    }

    @Transactional
    public void deleteArticle(@NonNull Long articleId, @NonNull Long userId) {
        if (!articleRepository.existsByIdAndAuthorId(articleId, userId)) {
            throw new ArticleNotFound(articleId);
        }
        articleRepository.deleteLikesById(articleId);
        articleRepository.deleteById(articleId);
        articleESRepository.deleteById(articleId);
    }

    public List<Article> getArticleListByUserId(@NonNull Long userId, int limit, String token) {
        Long ack = null;
        if (token != null && !token.isEmpty()) {
            ack = nextIdTokenConverter.parseNextId(token);
        }

        return articleRepository.findByAuthorId(userId, limit, ack);
    }

    public List<Article> getArticlesByUsername(@NonNull String username, int limit, String token) {
        Long userId = userRepository.findIdByUsername(username)
                .orElseThrow(() -> new UserNotFound(username));

        return this.getArticleListByUserId(userId, limit, token);
    }

    public Tuple<List<Article>, String> getArticlesByTag(@NonNull String tag, int limit,
            String token) {
        String searchAfterTag = null;
        if (token != null && !token.isEmpty()) {
            searchAfterTag = nextIdTokenConverter.parseNextId(token);
        }

        SearchAfterPage<Long> searchAfterPage =
                articleESRepository.findIdsByTag(tag, limit, searchAfterTag);

        List<Article> articles = articleRepository.findAllById(searchAfterPage.getContent());

        return new Tuple<List<Article>, String>(articles, searchAfterPage.getSearchAfterTag());
    }

    public boolean isUserLikeArticle(@NonNull Long userId, @NonNull Long articleId) {
        return articleRepository.existsByUserLikeAndArticle(articleId, userId);
    }

    public List<Long> getUserLikeArticleIds(Long userId, @NonNull List<Article> articles) {
        if (userId == null || articles.isEmpty()) {
            return List.of();
        }

        return articleRepository.findUserLikeArticleIds(userId, articles);
    }

    public Tuple<List<Article>, String> getArticlesByContext(String context, int limit,
            String token) {
        String searchAfterContext = null;
        if (token != null && !token.isEmpty()) {
            searchAfterContext = nextIdTokenConverter.parseNextId(token);
        }

        SearchAfterPage<Long> searchAfterPage =
                articleESRepository.findIdsByContext(context, limit, searchAfterContext);

        List<Article> articles = articleRepository.findAllById(searchAfterPage.getContent());

        return new Tuple<>(articles, searchAfterPage.getSearchAfterTag());
    }

    public void likeArticle(@NonNull Long articleId, @NonNull User user) {
        Article article =
                articleRepository.findByIdAndAuthorIdNotInLikeUsers(articleId, user.getId())
                        .orElseThrow(() -> new ArticleNotFound(articleId));

        System.err.println(article.getId());

        user.like(article);
        userRepository.save(user);
    }

    public void dislike(@NonNull Long articleId, @NonNull User user) {
        Article article = articleRepository.findByIdAndAuthorIdInLikeUsers(articleId, user.getId())
                .orElseThrow(() -> new ArticleNotFound(articleId));

        user.dislike(article);
        userRepository.save(user);
    }

    public List<Article> getLatestArticles(int limit) {
        return articleRepository.findLatestArticles(limit);
    }
}

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
        Article article = articleRepository.findFirstByIdAndAuthorId(articleId, userId)
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
        Article article = articleRepository.findFirstByIdAndAuthorId(articleId, userId)
                .orElseThrow(() -> new ArticleNotFound());

        article.update(data);
        articleRepository.save(article);
        articleESRepository.save(ArticleES.fromArticle(article));

        return article;
    }

    public void deleteArticle(@NonNull Long articleId, @NonNull Long userId) {
        if (!articleRepository.existsByIdAndAuthorId(articleId, userId)) {
            throw new ArticleNotFound(articleId);
        }
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

    public Tuple<List<Article>, String> getArticlesByTag(String tag, int limit, String token) {
        String searchAfterTag = null;
        if (token != null && !token.isEmpty()) {
            searchAfterTag = nextIdTokenConverter.parseNextId(token);
        }

        SearchAfterPage<Long> searchAfterPage =
                articleESRepository.findIdsByTag(tag, limit, searchAfterTag);

        List<Article> articles = articleRepository.findAllById(searchAfterPage.getContent());

        return new Tuple<List<Article>, String>(articles, searchAfterPage.getSearchAfterTag());
    }
}

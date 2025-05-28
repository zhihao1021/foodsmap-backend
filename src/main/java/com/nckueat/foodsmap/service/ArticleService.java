package com.nckueat.foodsmap.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import com.nckueat.foodsmap.component.SnowflakeId.SnowflakeIdGenerator;
import com.nckueat.foodsmap.exception.ArticleNotFound;
import com.nckueat.foodsmap.model.entity.Article;
import com.nckueat.foodsmap.model.entity.User;
import com.nckueat.foodsmap.repository.ArticlesRepository;
import com.nckueat.foodsmap.model.dto.request.ArticleCreate;
import com.nckueat.foodsmap.model.dto.request.ArticleUpdate;

@Service
public class ArticleService {

    @Autowired
    private SnowflakeIdGenerator snowflakeIdGenerator;
    @Autowired
    private ArticlesRepository articlesRepository;

    public Article ArticlesCreate(@NonNull ArticleCreate articlesCreate, User user) {
        Article article = Article.fromArticleCreate(snowflakeIdGenerator.nextId(), user.getId(),
                articlesCreate);
        article = articlesRepository.save(article);
        return article;
    }

    public Article ArticlesUpdate(@NonNull ArticleUpdate data, @PathVariable Long articleId) {
        Article article =
                articlesRepository.findById(articleId).orElseThrow(() -> new ArticleNotFound());
        data.getTitle().ifPresent(article::setTitle);
        data.getContext().ifPresent(article::setContext);
        data.getLike().ifPresent(article::setLike);
        data.getTags().ifPresent(article::setTags);
        articlesRepository.save(article);
        return article;
    }
}

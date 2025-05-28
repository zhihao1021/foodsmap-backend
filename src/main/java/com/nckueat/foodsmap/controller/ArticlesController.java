package com.nckueat.foodsmap.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;
//import org.springframework.data.mongodb.core.query.TextQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.domain.Sort;
//import org.springframework.stereotype.Service;

import com.nckueat.foodsmap.model.entity.User;
import com.nckueat.foodsmap.annotation.CurrentUser;

import com.nckueat.foodsmap.model.entity.Article;
import com.nckueat.foodsmap.service.ArticleService;
import com.nckueat.foodsmap.exception.ArticleNotFound;
import com.nckueat.foodsmap.exception.ArticleIdNotFound;
import com.nckueat.foodsmap.repository.ArticlesRepository;
import com.nckueat.foodsmap.model.dto.request.ArticleCreate;
import com.nckueat.foodsmap.model.dto.request.ArticleUpdate;
import com.nckueat.foodsmap.model.dto.request.ArticleSearch;

@RestController
@RequestMapping("/articles")
public class ArticlesController {

    @Autowired
    private ArticleService articlesService;
    @Autowired
    private ArticlesRepository  articlesRepository;
    @Autowired
    private MongoTemplate mongoTemplate;
    
    @PostMapping("create")
    public ResponseEntity<Article> ArticlesCreate(@RequestBody ArticleCreate data,@CurrentUser User user){
        URI location = URI.create("/user");
        return ResponseEntity.created(location).body(articlesService.ArticlesCreate(data,user));
    }

    @PostMapping("delete/{articleId}")
    public ResponseEntity<Void> ArticlesDelete(@PathVariable Long articleId) throws ArticleIdNotFound{
        if (!articlesRepository.existsById(articleId)) {
            throw new ArticleIdNotFound();
        }

        articlesRepository.deleteById(articleId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("update/{articleId}")
    public ResponseEntity<Article> ArticlesUpdate(@RequestBody ArticleUpdate data, @PathVariable Long articleId){
        URI location = URI.create("/user");
        return ResponseEntity.created(location).body(articlesService.ArticlesUpdate(data,articleId));
    }

    @PostMapping("search")
    public ResponseEntity<List<Article>> ArticlesSearch(@RequestBody ArticleSearch data) throws ArticleNotFound{

        // 創建查詢
        TextCriteria criteria = TextCriteria.forDefaultLanguage()
            .matchingAny(data.getSearchContext()); // 匹配任一詞

        Query query = TextQuery.queryText(criteria)
            .sortByScore()
            .with(Sort.by(Sort.Direction.DESC, "like"));

        List<Article> results = mongoTemplate.find(query, Article.class);
        //System.err.println("Search results: " + results);

        if (results.isEmpty()) {
            throw new ArticleNotFound();
        }

        System.out.println(results);

        return ResponseEntity.ok(results);
    }

    @PostMapping("recommend")
    public ResponseEntity<List<Article>> ArticlesRecommend() throws ArticleNotFound{

        Query query = new Query();
        query.with(Sort.by(Sort.Direction.DESC, "like")).limit(10);
        List<Article> results = mongoTemplate.find(query, Article.class);

        if (results.isEmpty()) {
            throw new ArticleNotFound();
        }

        return ResponseEntity.ok(results);
    }
    
}

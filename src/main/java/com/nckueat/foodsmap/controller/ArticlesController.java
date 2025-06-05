package com.nckueat.foodsmap.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;
// import org.springframework.data.mongodb.core.query.TextQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.domain.Sort;
// import org.springframework.stereotype.Service;

import com.nckueat.foodsmap.model.entity.User;
import com.nckueat.foodsmap.annotation.CurrentUser;

import com.nckueat.foodsmap.model.entity.Article;
import com.nckueat.foodsmap.service.ArticleService;
import com.nckueat.foodsmap.exception.ArticleNotFound;
import com.nckueat.foodsmap.repository.UserRepository;
import com.nckueat.foodsmap.repository.ArticlesRepository;
import com.nckueat.foodsmap.model.dto.request.ArticleCreate;
import com.nckueat.foodsmap.model.dto.request.ArticleUpdate;
import com.nckueat.foodsmap.model.dto.vo.ArticleRead;
import com.nckueat.foodsmap.model.dto.vo.UserRead;
import com.nckueat.foodsmap.model.dto.request.ArticleSearch;

@RestController
@RequestMapping("/articles")
public class ArticlesController {

    @Autowired
    private ArticleService articlesService;
    @Autowired
    private ArticlesRepository articlesRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MongoTemplate mongoTemplate;

    @PostMapping("")
    public ResponseEntity<ArticleRead> ArticlesCreate(@RequestBody ArticleCreate data,
            @CurrentUser User user) {
        URI location = URI.create("/user");
        return ResponseEntity.created(location).body(articlesService.ArticlesCreate(data, user));
    }

    @DeleteMapping("{articleId}")
    public ResponseEntity<Void> ArticlesDelete(@PathVariable Long articleId)
            throws ArticleNotFound {
        if (!articlesRepository.existsById(articleId)) {
            throw new ArticleNotFound();
        }

        articlesRepository.deleteById(articleId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("{articleId}")
    public ResponseEntity<ArticleRead> ArticlesUpdate(@RequestBody ArticleUpdate data,
            @PathVariable Long articleId) {
        URI location = URI.create("/user");
        return ResponseEntity.created(location)
                .body(articlesService.ArticlesUpdate(data, articleId));
    }

    @PostMapping("search/text")
    public ResponseEntity<List<ArticleRead>> ArticlesSearch(@RequestBody ArticleSearch data)
            throws ArticleNotFound {
        
        String[] searchContext = articlesService.spiltSearchText(data.getSearchContext());
        TextCriteria criteria =
                TextCriteria.forDefaultLanguage().matchingAny(searchContext); // 匹配任一詞

        Query query = TextQuery.queryText(criteria)
                .sortByScore()
                .addCriteria(Criteria.where("score").gt(0))
                .with(Sort.by(Sort.Direction.DESC, "like"));
        List<Article> searchResults = mongoTemplate.find(query, Article.class);
        // System.err.println("Search results: " + results);

        if (searchResults.isEmpty()) {
            throw new ArticleNotFound();
        }

        List<ArticleRead> results = new ArrayList<>();
        for (Article article : searchResults) {
            System.out.println("Found article: " + article.getTitle());
            results.add(Article.toArticleRead(article));
        }

        return ResponseEntity.ok(results);
    }

    @PostMapping("search/author")
    public ResponseEntity<UserRead> ArticlesAuthor(@RequestBody ArticleSearch data)
            throws ArticleNotFound {
        String searchContext = data.getSearchContext().replaceAll("[\\s,|+_\\-]+", "");

        User author = userRepository.findByUsername(searchContext)
                .orElseThrow(() -> new ArticleNotFound("Author not found"));

        System.out.println(author);

        UserRead authorRead = author.toUserRead();

        return ResponseEntity.ok(authorRead);
    }

    @PostMapping("search/tag")
    public ResponseEntity<List<ArticleRead>> ArticlesTag(@RequestBody ArticleSearch data)
            throws ArticleNotFound {

        String[] searchContext = articlesService.spiltSearchText(data.getSearchContext());
        System.out.println("Searching for articles with tags: " + String.join(", ", searchContext));
        List<ArticleRead> results = articlesService.ArticleSearchTag(searchContext);
        results.forEach(article -> System.out.println("Found article: " + article.getTitle()));
        if (results.isEmpty()) {
            throw new ArticleNotFound();
        }

        System.out.println(results);

        return ResponseEntity.ok(results);
    }

    @GetMapping("recommend")
    public ResponseEntity<List<ArticleRead>> ArticlesRecommend() throws ArticleNotFound {

        Query query = new Query();
        query.with(Sort.by(Sort.Direction.DESC, "like")).limit(10);
        List<Article> recommendArticle = mongoTemplate.find(query, Article.class);

        if (recommendArticle.isEmpty()) {
            throw new ArticleNotFound();
        }

        List<ArticleRead> results = new ArrayList<>();

        for (Article article : recommendArticle) {
            results.add(Article.toArticleRead(article));
        }

        return ResponseEntity.ok(results);
    }

}

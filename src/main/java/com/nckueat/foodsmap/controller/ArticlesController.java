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
import org.yaml.snakeyaml.util.Tuple;
import org.springframework.beans.factory.annotation.Autowired;

import com.nckueat.foodsmap.model.entity.User;
import com.nckueat.foodsmap.annotation.CurrentUser;
import com.nckueat.foodsmap.annotation.CurrentUserId;
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
    public ResponseEntity<ArticleRead> getArticle(@NonNull @PathVariable Long articleId) {
        return ResponseEntity.ok().body(articlesService.findArticleById(articleId).toArticleRead());
    }

    @PutMapping("by-id/{articleId}")
    public ResponseEntity<ArticleRead> updateArticle(@RequestBody ArticleUpdate data,
            @NonNull @PathVariable Long articleId, @CurrentUserId Long userId) {
        URI location = URI.create(String.format("/article/by-id/%s", articleId));
        return ResponseEntity.created(location)
                .body(articlesService.updateArticle(articleId, data, userId).toArticleRead());
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
            @RequestParam(required = false) String token) {

        Tuple<List<Article>, String> resultPair =
                articlesService.getArticlesByTag(tagName, limit, token);
        List<Article> articles = resultPair._1();
        String searchAfterTag = resultPair._2();

        String newToken =
                articles.size() < limit ? null : nextIdTokenConverter.getNextToken(searchAfterTag);

        return ResponseEntity.ok(new ListResponse<>(
                articles.stream().map(Article::toArticleRead).toList(), newToken));
    }

    // @GetMapping("by-tag/{tagName}")
    // public ResponseEntity<ListResponse<ArticleRead>> getArticlesByTag() {

    // }

    // @PostMapping("search/text")
    // public ResponseEntity<List<ArticleRead>> ArticlesSearch(@RequestBody ArticleSearch data)
    // throws ArticleNotFound {

    // String[] searchContext = articlesService.spiltSearchText(data.getSearchContext());
    // TextCriteria criteria = TextCriteria.forDefaultLanguage().matchingAny(searchContext); //
    // 匹配任一詞

    // Query query = TextQuery.queryText(criteria).sortByScore()
    // .addCriteria(Criteria.where("score").gt(0))
    // .with(Sort.by(Sort.Direction.DESC, "like"));
    // List<Article> searchResults = mongoTemplate.find(query, Article.class);
    // // System.err.println("Search results: " + results);

    // if (searchResults.isEmpty()) {
    // throw new ArticleNotFound();
    // }

    // List<ArticleRead> results = new ArrayList<>();
    // for (Article article : searchResults) {
    // System.out.println("Found article: " + article.getTitle());
    // results.add(article.toArticleRead());
    // }

    // return ResponseEntity.ok(results);
    // }

    // @PostMapping("search/author")
    // public ResponseEntity<UserRead> ArticlesAuthor(@RequestBody ArticleSearch data)
    // throws ArticleNotFound {
    // String searchContext = data.getSearchContext().replaceAll("[\\s,|+_\\-]+", "");

    // User author = userRepository.findByUsername(searchContext)
    // .orElseThrow(() -> new UserNotFound("Author not found"));

    // System.out.println(author);

    // UserRead authorRead = author.toUserRead();

    // return ResponseEntity.ok(authorRead);
    // }

    // @PostMapping("search/tag")
    // public ResponseEntity<List<ArticleRead>> ArticlesTag(@RequestBody ArticleSearch data)
    // throws ArticleNotFound {

    // String[] searchContext = articlesService.spiltSearchText(data.getSearchContext());
    // System.out.println("Searching for articles with tags: " + String.join(", ", searchContext));
    // List<ArticleRead> results = articlesService.ArticleSearchTag(searchContext);
    // results.forEach(article -> System.out.println("Found article: " + article.getTitle()));
    // if (results.isEmpty()) {
    // throw new ArticleNotFound();
    // }

    // System.out.println(results);

    // return ResponseEntity.ok(results);
    // }

    // @GetMapping("recommend")
    // public ResponseEntity<List<ArticleRead>> ArticlesRecommend() throws ArticleNotFound {

    // Query query = new Query();
    // query.with(Sort.by(Sort.Direction.DESC, "like")).limit(10);
    // List<Article> recommendArticle = mongoTemplate.find(query, Article.class);

    // if (recommendArticle.isEmpty()) {
    // throw new ArticleNotFound();
    // }

    // List<ArticleRead> results = new ArrayList<>();

    // for (Article article : recommendArticle) {
    // results.add(article.toArticleRead());
    // }

    // return ResponseEntity.ok(results);
    // }

    // @GetMapping("recommend/tag")
    // public ResponseEntity<List<String>> TagRecommend() throws ArticleNotFound {
    // List<String> tags = articlesService.findTop20Tags();
    // return ResponseEntity.ok(tags);
    // }

}

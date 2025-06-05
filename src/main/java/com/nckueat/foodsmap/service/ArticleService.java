package com.nckueat.foodsmap.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.domain.Sort;

import com.nckueat.foodsmap.component.SnowflakeId.SnowflakeIdGenerator;
import com.nckueat.foodsmap.exception.ArticleNotFound;
import com.nckueat.foodsmap.model.entity.Article;
import com.nckueat.foodsmap.model.entity.User;
import com.nckueat.foodsmap.repository.ArticlesRepository;
import com.nckueat.foodsmap.model.dto.request.ArticleCreate;
import com.nckueat.foodsmap.model.dto.request.ArticleUpdate;
import com.nckueat.foodsmap.model.dto.vo.ArticleRead;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class ArticleService {

    @Autowired
    private SnowflakeIdGenerator snowflakeIdGenerator;
    @Autowired
    private ArticlesRepository articlesRepository;
    @Autowired
    private MongoTemplate mongoTemplate;

    public ArticleRead ArticlesCreate(@NonNull ArticleCreate articlesCreate, User user) {
        Article article = Article.fromArticleCreate(snowflakeIdGenerator.nextId(), user.getId(),
                articlesCreate, spiltTags(articlesCreate.getContext()));
        article = articlesRepository.save(article);
        ArticleRead articleRead = Article.toArticleRead(article);
        return articleRead;
    }

    public ArticleRead ArticlesUpdate(@NonNull ArticleUpdate data, @PathVariable Long articleId) {
        Article article =
                articlesRepository.findById(articleId).orElseThrow(() -> new ArticleNotFound());
        data.getTitle().ifPresent(article::setTitle);
        data.getContext().ifPresent(article::setContext);
        data.getLike().ifPresent(article::setLike);
        String[] tags = spiltTags(data.getContext().orElse(""));
        if (tags.length > 0) {
            article.setTags(tags);
        }else{
            article.setTags(new String[0]);
        }
        articlesRepository.save(article);

        ArticleRead articleRead = Article.toArticleRead(article);
        return articleRead;
    }

    public List<ArticleRead> ArticleSearchTag(String[] tag){
        Query query = new Query();
        List<Criteria> regexCriterias = new ArrayList<>();
        
        for (String t : tag) {
            regexCriterias.add(
                Criteria.where("tags").regex(".*" + Pattern.quote(t) + ".*", "i")
            );
        }

        query.addCriteria(new Criteria().orOperator(regexCriterias.toArray(new Criteria[0])))
            .with(Sort.by(Sort.Direction.DESC, "like"));

        List<Article> articles = mongoTemplate.find(query, Article.class);
        List<ArticleRead> results = new ArrayList<>();

        for (Article article : articles) {
            results.add(Article.toArticleRead(article));
        }

        return results;
    }

    public String[] spiltTags(String inputTags) {
	    inputTags = inputTags.replaceAll("#\\s+", "#");
	    List<String> tags = new ArrayList<>();

	    Pattern pattern = Pattern.compile("#(\\w+(?:\\+\\w+)*)");
	    Matcher matcher = pattern.matcher(inputTags);

	    while (matcher.find()) {
	        tags.add(matcher.group(1));
	    }
        return tags.toArray(new String[0]);
	}

    public String[] spiltSearchText(String seaechText) {
	    String seaech = seaechText.replaceAll("[\\s,|+_\\-]+", " ");
	    return seaech.split(" ");
    }
}

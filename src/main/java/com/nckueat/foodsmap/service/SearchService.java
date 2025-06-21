package com.nckueat.foodsmap.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.nckueat.foodsmap.repository.elasticsearch.ArticleESRepository;
import com.nckueat.foodsmap.repository.postgresql.ArticleRepository;
import com.nckueat.foodsmap.model.entity.Article;



@Service
public class SearchService {
    @Autowired
    private ArticleESRepository articleESRepository;

    @Autowired
    private ArticleRepository articleRepository;

    public List<String> getPopularTags(int limit) {
        return articleESRepository.findPopularTags(limit);
    }

    public List<Article> getLatestArticles(int limit) {
        return articleRepository.findLatestArticles(limit);
    }
}

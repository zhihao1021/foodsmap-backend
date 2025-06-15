package com.nckueat.foodsmap.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.nckueat.foodsmap.repository.elasticsearch.ArticleESRepository;

@Service
public class SearchService {
    @Autowired
    private ArticleESRepository articleESRepository;

    public List<String> getPopularTags(int limit) {
        return articleESRepository.findPopularTags(limit);
    }
}

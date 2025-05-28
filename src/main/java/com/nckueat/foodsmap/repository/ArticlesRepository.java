package com.nckueat.foodsmap.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.nckueat.foodsmap.model.entity.Article;

public interface ArticlesRepository extends MongoRepository<Article, Long> {

    boolean existsById(Long id);

    boolean existsByAuthorID(Long authorID);

    Optional<Article> findById(Long id);

    Optional<Article> findByAuthorID(String authorID);
}

package com.nckueat.foodsmap.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.lang.NonNull;
import com.nckueat.foodsmap.model.entity.Article;

public interface ArticlesRepository extends MongoRepository<Article, Long> {

    boolean existsById(@NonNull Long id);

    boolean existsByAuthorID(Long authorID);

    @NonNull
    Optional<Article> findById(@NonNull Long id);

    Optional<Article> findByAuthorID(String authorID);
}

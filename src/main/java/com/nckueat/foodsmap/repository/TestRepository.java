package com.nckueat.foodsmap.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.lang.NonNull;
import com.nckueat.foodsmap.model.entity.Test;

public interface TestRepository extends ElasticsearchRepository<Test, Long> {
    Test findByNameContaining(@NonNull String name);
}

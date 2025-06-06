package com.nckueat.foodsmap.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.lang.NonNull;
import com.nckueat.foodsmap.model.entity.Avatar;

public interface AvatarRepository extends MongoRepository<Avatar, Long> {
    boolean existsById(@NonNull Long userId);
    @NonNull
    Optional<Avatar> findById(@NonNull Long userId);
    void deleteByUserId(Long userId);
}

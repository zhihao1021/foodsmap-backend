package com.nckueat.foodsmap.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.nckueat.foodsmap.model.entity.Avatar;

public interface AvatarRepository extends MongoRepository<Avatar, Long> {
    boolean existsByUserId(Long userId);
    Optional<Avatar> findByUserId(Long userId);
}

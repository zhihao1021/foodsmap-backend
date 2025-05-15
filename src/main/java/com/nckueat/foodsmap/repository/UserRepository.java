package com.nckueat.foodsmap.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.nckueat.foodsmap.model.entity.User;

public interface UserRepository extends MongoRepository<User, Long> {
    <T> Optional<T> findById(Long id, Class<T> dto);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);
}

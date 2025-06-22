package com.nckueat.foodsmap.repository.postgresql;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.nckueat.foodsmap.model.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    @Query("SELECT u.id FROM User u WHERE u.username = :username")
    Optional<Long> findIdByUsername(@Param("username") String username);

    @Query("SELECT a.id FROM User u JOIN u.likedArticles a WHERE u.id = :userId")
    List<Long> findLikedArticleIdsByUserId(@Param("userId") Long userId);

    @Query("SELECT u FROM User u WHERE u.displayName LIKE %:displayName%")
    List<User> findByDisplayNameContaining(@Param("displayName") String displayName);
}

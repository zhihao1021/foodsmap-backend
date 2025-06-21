package com.nckueat.foodsmap.repository.postgresql;

import java.util.Optional;
import java.util.List;
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

    // @Query("SELECT * FROM User u WHERE u.desplayName = :desplayName")
    @Query("SELECT u FROM User u WHERE u.displayName LIKE %:displayName%")
    Optional<List<User>> findByDisplayNameContaining(@Param("displayName") String displayName);
}

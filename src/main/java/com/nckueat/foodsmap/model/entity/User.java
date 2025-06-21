package com.nckueat.foodsmap.model.entity;

import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.nckueat.foodsmap.model.dto.request.UserCreate;
import com.nckueat.foodsmap.model.dto.vo.GlobalUserView;
import com.nckueat.foodsmap.model.dto.vo.UserRead;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Builder
@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @Column(unique = true, nullable = false)
    private Long id;

    @NonNull
    @Size(min = 5, max = 30)
    @Column(unique = true, nullable = false, length = 30)
    private String username;

    @NonNull
    @Column(unique = true, nullable = false, columnDefinition = "TEXT")
    private String email;

    @Column(unique = true, columnDefinition = "TEXT")
    private String googleId;

    @NonNull
    @Size(min = 1, max = 64)
    @Column(nullable = false, length = 64)
    private String displayName;

    @NonNull
    @Column(nullable = false, columnDefinition = "TEXT")
    private String hashedPassword;

    @Builder.Default
    @Column(columnDefinition = "TEXT")
    private String totpSecret = null;

    @NonNull
    @Builder.Default
    @Column(nullable = false)
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<Article> articles = new ArrayList<>();

    @NonNull
    @Builder.Default
    @Column(nullable = false)
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(name = "user_likes", joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "article_id"))
    @OrderColumn(name = "index")
    private List<Article> likedArticles = new ArrayList<>();

    public static User fromUserCreate(Long userId, UserCreate userCreate) {
        return User.builder().id(userId).username(userCreate.getUsername())
                .email(userCreate.getEmail()).displayName(userCreate.getDisplayName())
                .hashedPassword(new BCryptPasswordEncoder(10).encode(userCreate.getPassword()))
                .build();
    }

    public UserRead toUserRead() {
        return new UserRead(this.id.toString(), this.username, this.email, this.displayName);
    }

    public GlobalUserView toGlobalUserView() {
        return new GlobalUserView(this.id.toString(), this.username, this.displayName);
    }

    public void setPassword(String password) {
        this.hashedPassword = new BCryptPasswordEncoder(10).encode(password);
    }

    public boolean checkPassword(String password) {
        return new BCryptPasswordEncoder(10).matches(password, this.hashedPassword);
    }

    public void like(Article article) {
        this.likedArticles.add(article);
    }

    public void dislike(Article article) {
        this.likedArticles.remove(article);
    }
}

package com.nckueat.foodsmap.model.entity;

import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.nckueat.foodsmap.model.dto.request.UserCreate;
import com.nckueat.foodsmap.model.dto.vo.GlobalUserView;
import com.nckueat.foodsmap.model.dto.vo.UserRead;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
    @Column(unique = true, nullable = false)
    private String username;

    @NonNull
    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true)
    private String googleId;

    @NonNull
    @Size(min = 1, max = 64)
    @Column(nullable = false)
    private String displayName;

    @NonNull
    @Column(nullable = false)
    private String hashedPassword;

    @Builder.Default
    private String totpSecret = null;

    // @NonNull
    // @Builder.Default
    // @ElementCollection
    // private Article[] likeArticles = new Article[0];

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
}

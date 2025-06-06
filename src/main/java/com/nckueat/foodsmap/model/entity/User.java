package com.nckueat.foodsmap.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.nckueat.foodsmap.model.dto.request.UserCreate;
import com.nckueat.foodsmap.model.dto.vo.GlobalUserView;
import com.nckueat.foodsmap.model.dto.vo.UserRead;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
@Document(collection = "users")
public class User {
    @Id
    private Long id;

    @NonNull
    @Indexed(unique = true)
    private String username;

    @NonNull
    @Indexed(unique = true)
    private String email;

    @Indexed(unique = true)
    private String googleId;

    @NonNull
    private String displayName;

    @NonNull
    private String hashedPassword;

    @Builder.Default
    private String totpSecret = null;

    // @Builder.Default
    // private String avatarContentType = null;

    // @Builder.Default
    // private Binary avatar = null;

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

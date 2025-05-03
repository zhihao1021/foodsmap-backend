package com.nckueat.foodsmap.model.enitiy;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.nckueat.foodsmap.model.dto.request.UserCreate;
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

    public static User fromUserCreate(Long userId, UserCreate userCreate) {
        return User.builder().id(userId).username(userCreate.getUsername())
                .email(userCreate.getEmail()).displayName(userCreate.getDisplayName())
                .hashedPassword(new BCryptPasswordEncoder(10).encode(userCreate.getPassword()))
                .build();
    }

    public boolean checkPassword(String password) {
        return new BCryptPasswordEncoder(10).matches(password, this.hashedPassword);
    }
}

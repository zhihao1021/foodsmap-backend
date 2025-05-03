package com.nckueat.foodsmap.model.dto.vo;

import com.nckueat.foodsmap.model.enitiy.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRead {
    private final String id;
    private final String username;
    private final String email;
    private final String displayName;

    public static UserRead fromUser(User user) {
        return UserRead.builder().id(user.getId().toString()).username(user.getUsername())
                .email(user.getEmail()).displayName(user.getDisplayName()).build();
    }
}

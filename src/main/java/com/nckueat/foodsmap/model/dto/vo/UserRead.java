package com.nckueat.foodsmap.model.dto.vo;

import lombok.Data;
import lombok.NonNull;

@Data
public class UserRead {
    @NonNull
    private final String id;

    @NonNull
    private final String username;

    @NonNull
    private final String email;

    @NonNull
    private final String displayName;
}

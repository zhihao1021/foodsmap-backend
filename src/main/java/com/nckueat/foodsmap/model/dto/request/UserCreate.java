package com.nckueat.foodsmap.model.dto.request;

import lombok.Data;
import lombok.NonNull;

@Data
public class UserCreate {
    @NonNull
    private final String email;
    @NonNull
    private final String username;
    @NonNull
    private final String displayName;
    @NonNull
    private final String password;
    private final boolean noExpiration = false;
    @NonNull
    private final String emailValidCode;
    @NonNull
    private final String identifyCode;
}

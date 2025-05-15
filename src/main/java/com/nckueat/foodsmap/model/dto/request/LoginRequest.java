package com.nckueat.foodsmap.model.dto.request;

import lombok.Data;
import lombok.NonNull;
import com.nckueat.foodsmap.types.LoginMethod;

@Data
public class LoginRequest {
    @NonNull
    private final String emailOrUsername;
    @NonNull
    private final String code;
    @NonNull
    private final LoginMethod method;
    private final boolean noExpiration = false;
}

package com.nckueat.foodsmap.model.dto.request;

import com.mongodb.lang.NonNull;
import lombok.Data;

@Data
public class CheckUsernameRequest {
    @NonNull
    private final String username;
}

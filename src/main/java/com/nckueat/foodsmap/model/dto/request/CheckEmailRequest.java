package com.nckueat.foodsmap.model.dto.request;

import lombok.Data;
import lombok.NonNull;

@Data
public class CheckEmailRequest {
    @NonNull
    private final String email;
}

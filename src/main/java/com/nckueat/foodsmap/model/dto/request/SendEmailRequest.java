package com.nckueat.foodsmap.model.dto.request;

import lombok.Data;
import lombok.NonNull;

@Data
public class SendEmailRequest {
    @NonNull
    private final String email;

    @NonNull
    private final String code;
}

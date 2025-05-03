package com.nckueat.foodsmap.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Jwt {
    private final String access_token;
    private final String token_type = "Bearer";
}

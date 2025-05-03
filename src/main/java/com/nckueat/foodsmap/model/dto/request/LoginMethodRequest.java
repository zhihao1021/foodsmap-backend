package com.nckueat.foodsmap.model.dto.request;

import lombok.Data;
import lombok.NonNull;

@Data
public class LoginMethodRequest {
    @NonNull
    private final String data;
}

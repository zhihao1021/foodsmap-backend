package com.nckueat.foodsmap.model.dto.response;

import java.util.List;

import lombok.Data;

import com.nckueat.foodsmap.types.LoginMethod;

@Data
public class LoginMethodsResponse {
    private final List<LoginMethod> methods;
}

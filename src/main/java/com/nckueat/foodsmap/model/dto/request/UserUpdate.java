package com.nckueat.foodsmap.model.dto.request;

import java.util.Optional;
import lombok.Data;

@Data
public class UserUpdate {
    private final Optional<String> email;
    private final Optional<String> displayName;
    private final Optional<String> password;
    private final Optional<String> newPassword;
    private final Optional<String> oldEmailValidCode;
    private final Optional<String> oldIdentifyCode;
    private final Optional<String> emailValidCode;
    private final Optional<String> identifyCode;
}

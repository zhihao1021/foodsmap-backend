package com.nckueat.foodsmap.exception;

import org.springframework.http.HttpStatus;
import com.nckueat.foodsmap.exceptionHandler.HTTPException;

public class PasswordNotMatch extends HTTPException {
    public PasswordNotMatch() {
        super(HttpStatus.BAD_REQUEST, "Password not match");
    }

    public PasswordNotMatch(String username) {
        super(HttpStatus.BAD_REQUEST, String.format("%s password not match", username));
    }
}

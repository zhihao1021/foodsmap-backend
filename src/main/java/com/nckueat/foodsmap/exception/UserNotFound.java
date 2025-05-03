package com.nckueat.foodsmap.exception;

import org.springframework.http.HttpStatus;
import com.nckueat.foodsmap.exceptionHandler.HTTPException;

public class UserNotFound extends HTTPException {
    public UserNotFound(String username) {
        super(HttpStatus.NOT_FOUND, String.format("User \"%s\" not found", username));
    }

    public UserNotFound() {
        this("Unknow");
    }
}

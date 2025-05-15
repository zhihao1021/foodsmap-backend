package com.nckueat.foodsmap.exception;

import org.springframework.http.HttpStatus;
import com.nckueat.foodsmap.exceptionHandler.HTTPException;

public class AvatarNotFound extends HTTPException {
    public AvatarNotFound() {
        super(HttpStatus.NOT_FOUND, "Avatar not found");
    }
}

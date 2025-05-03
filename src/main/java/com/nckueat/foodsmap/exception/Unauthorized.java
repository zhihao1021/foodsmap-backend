package com.nckueat.foodsmap.exception;

import org.springframework.http.HttpStatus;
import com.nckueat.foodsmap.exceptionHandler.HTTPException;

public class Unauthorized extends HTTPException {
    public Unauthorized() {
        super(HttpStatus.UNAUTHORIZED, "Unauthorized");
    }
}

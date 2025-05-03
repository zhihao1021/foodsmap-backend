package com.nckueat.foodsmap.exception;

import org.springframework.http.HttpStatus;

import com.nckueat.foodsmap.exceptionHandler.HTTPException;

public class UsernameIllegal extends HTTPException {
    public UsernameIllegal() {
        super(HttpStatus.BAD_REQUEST, "Username illegal");
    }
}

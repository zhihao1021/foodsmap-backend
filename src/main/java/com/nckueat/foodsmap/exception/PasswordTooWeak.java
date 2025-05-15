package com.nckueat.foodsmap.exception;

import org.springframework.http.HttpStatus;

import com.nckueat.foodsmap.exceptionHandler.HTTPException;

public class PasswordTooWeak extends HTTPException {
    public PasswordTooWeak() {
        super(HttpStatus.BAD_REQUEST, "Password too weak");
    }
}

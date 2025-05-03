package com.nckueat.foodsmap.exception;

import org.springframework.http.HttpStatus;

import com.nckueat.foodsmap.exceptionHandler.HTTPException;

public class UserAlreadyExist extends HTTPException {
    public UserAlreadyExist(String username) {
        super(HttpStatus.CONFLICT, String.format("User \"%s\" already exist", username));
    }

    public UserAlreadyExist() {
        this("Unknow");
    }
}

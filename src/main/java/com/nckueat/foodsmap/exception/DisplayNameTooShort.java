package com.nckueat.foodsmap.exception;

import org.springframework.http.HttpStatus;

import com.nckueat.foodsmap.exceptionHandler.HTTPException;

public class DisplayNameTooShort extends HTTPException {
    public DisplayNameTooShort() {
        super(HttpStatus.BAD_REQUEST, "Display name too short");
    }
}

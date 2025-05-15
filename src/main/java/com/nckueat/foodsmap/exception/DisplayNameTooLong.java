package com.nckueat.foodsmap.exception;

import org.springframework.http.HttpStatus;

import com.nckueat.foodsmap.exceptionHandler.HTTPException;

public class DisplayNameTooLong extends HTTPException {
    public DisplayNameTooLong() {
        super(HttpStatus.BAD_REQUEST, "Display name too long");
    }
}

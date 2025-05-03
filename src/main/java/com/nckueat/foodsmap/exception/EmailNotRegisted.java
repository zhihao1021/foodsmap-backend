package com.nckueat.foodsmap.exception;

import org.springframework.http.HttpStatus;
import com.nckueat.foodsmap.exceptionHandler.HTTPException;

public class EmailNotRegisted extends HTTPException {
    public EmailNotRegisted(String email) {
        super(HttpStatus.BAD_REQUEST, String.format("Email \"%s\" not registered", email));
    }
}

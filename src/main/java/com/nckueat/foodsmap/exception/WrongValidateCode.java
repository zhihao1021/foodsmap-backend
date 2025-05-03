package com.nckueat.foodsmap.exception;

import org.springframework.http.HttpStatus;
import com.nckueat.foodsmap.exceptionHandler.HTTPException;

public class WrongValidateCode extends HTTPException {
    public WrongValidateCode() {
        super(HttpStatus.BAD_REQUEST, "Wrong validate code");
    }

    public WrongValidateCode(String email) {
        super(HttpStatus.BAD_REQUEST, String.format("%s validate code not match", email));
    }
    
}
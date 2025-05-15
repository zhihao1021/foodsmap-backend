package com.nckueat.foodsmap.exception;

import org.springframework.http.HttpStatus;
import com.nckueat.foodsmap.exceptionHandler.HTTPException;

public class WrongEmailFormat extends HTTPException {
    public WrongEmailFormat() {
        super(HttpStatus.BAD_REQUEST, "Wrong email format");
    }

}

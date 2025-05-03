package com.nckueat.foodsmap.exception;

import org.springframework.http.HttpStatus;
import com.nckueat.foodsmap.exceptionHandler.HTTPException;

public class UnknownLoginMethod extends HTTPException {
    public UnknownLoginMethod(String method) {
        super(HttpStatus.BAD_REQUEST, "Unknown login method: " + method);
    }

    public UnknownLoginMethod() {
        super(HttpStatus.BAD_REQUEST, "Unknown login method");
    }
}

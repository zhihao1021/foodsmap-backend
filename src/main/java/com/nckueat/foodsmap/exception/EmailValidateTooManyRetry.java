package com.nckueat.foodsmap.exception;

import org.springframework.http.HttpStatus;
import com.nckueat.foodsmap.exceptionHandler.HTTPException;

public class EmailValidateTooManyRetry extends HTTPException {
    public EmailValidateTooManyRetry(String email) {
        super(HttpStatus.TOO_MANY_REQUESTS,
                String.format("Email %s validate too many retry", email));
    }
}

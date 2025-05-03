package com.nckueat.foodsmap.exception;

import org.springframework.http.HttpStatus;
import com.nckueat.foodsmap.exceptionHandler.HTTPException;

public class TooFrequentResends extends HTTPException {
    public TooFrequentResends() {
        super(HttpStatus.TOO_MANY_REQUESTS, "Too many requests, please try again later");
    }

    public TooFrequentResends(String email) {
        super(HttpStatus.TOO_MANY_REQUESTS,
                String.format("%s too many requests, please try again later", email));
    }
}

package com.nckueat.foodsmap.exception;

import org.springframework.http.HttpStatus;
import com.nckueat.foodsmap.exceptionHandler.HTTPException;

public class ContentLengthRequired extends HTTPException {
    public ContentLengthRequired() {
        super(HttpStatus.LENGTH_REQUIRED, "Content-Length header is required for this request.");
    }
}

package com.nckueat.foodsmap.exception;

import org.springframework.http.HttpStatus;
import com.nckueat.foodsmap.exceptionHandler.HTTPException;

public class UnsupportedMediaType extends HTTPException {
    public UnsupportedMediaType(String filename) {
        super(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                String.format("Unsupported media type for file: %s", filename));
    }
}

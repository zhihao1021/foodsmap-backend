package com.nckueat.foodsmap.exception;

import org.springframework.http.HttpStatus;
import com.nckueat.foodsmap.exceptionHandler.HTTPException;

public class FileSizeLimitExceeded extends HTTPException {
    public FileSizeLimitExceeded(String filename, long limit) {
        super(HttpStatus.PAYLOAD_TOO_LARGE,
                String.format("File %s size exceeds the limit of %d bytes", filename, limit));
    }

    public FileSizeLimitExceeded(long limit) {
        super(HttpStatus.PAYLOAD_TOO_LARGE,
                String.format("File size exceeds the limit of %d bytes", limit));
    }
}

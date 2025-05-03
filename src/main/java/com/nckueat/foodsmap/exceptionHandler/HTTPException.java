package com.nckueat.foodsmap.exceptionHandler;

import org.springframework.http.HttpStatus;

public class HTTPException extends RuntimeException {
    private HttpStatus status;
    private String detail;

    public HTTPException(HttpStatus status, String detail) {
        super(detail);
        this.status = status;
        this.detail = detail;
    }

    public CustomErrorResponse toErrorResponse() {
        return new CustomErrorResponse(status, detail);
    }
}

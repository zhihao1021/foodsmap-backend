package com.nckueat.foodsmap.exceptionHandler;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

public class CustomErrorResponse {
    private long timestamp;
    private HttpStatusCode statusCode;
    private String detail;

    public CustomErrorResponse(HttpStatusCode statusCode, String detail) {
        this.timestamp = System.currentTimeMillis();
        this.statusCode = statusCode;
        this.detail = detail;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getStatusCode() {
        return statusCode.value();
    }

    public String getDetail() {
        return detail;
    }

    public ResponseEntity<CustomErrorResponse> toResponseEntity() {
        return new ResponseEntity<>(this, this.statusCode);
    }
}

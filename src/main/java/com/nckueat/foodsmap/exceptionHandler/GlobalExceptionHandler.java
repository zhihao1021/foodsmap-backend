package com.nckueat.foodsmap.exceptionHandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomErrorResponse> handleException(Exception e) {
        HttpStatusCode statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
        if (e instanceof ErrorResponse) {
            statusCode = ((ErrorResponse) e).getStatusCode();
        }

        if (!(e instanceof HTTPException)) {
            e.printStackTrace();
        }

        CustomErrorResponse errorResponse = new CustomErrorResponse(statusCode, e.getMessage());

        return errorResponse.toResponseEntity();
    }

    @ExceptionHandler(HTTPException.class)
    public ResponseEntity<CustomErrorResponse> handleHTTPException(HTTPException e) {
        CustomErrorResponse errorResponse = e.toErrorResponse();

        return errorResponse.toResponseEntity();
    }
}

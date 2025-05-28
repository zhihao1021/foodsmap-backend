package com.nckueat.foodsmap.exception;

import org.springframework.http.HttpStatus;
import com.nckueat.foodsmap.exceptionHandler.HTTPException;

public class ArticleIdNotFound extends HTTPException {
    public ArticleIdNotFound() {
        super(HttpStatus.NOT_FOUND, "ArticleId not found");
    }
}

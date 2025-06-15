package com.nckueat.foodsmap.exception;

import org.springframework.http.HttpStatus;
import com.nckueat.foodsmap.exceptionHandler.HTTPException;

public class ArticleNotFound extends HTTPException {
    public ArticleNotFound() {
        super(HttpStatus.NOT_FOUND, "Article not found");
    }

    public ArticleNotFound(Long articleId) {
        super(HttpStatus.NOT_FOUND, String.format("Article with ID %d not found", articleId));
    }
}

package com.nckueat.foodsmap.exception;
import org.springframework.http.HttpStatus;
import com.nckueat.foodsmap.exceptionHandler.HTTPException;

public class ArticleNotFound extends HTTPException {
    public ArticleNotFound() {
        super(HttpStatus.NOT_FOUND, "Article not found");
    }

    public ArticleNotFound(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}

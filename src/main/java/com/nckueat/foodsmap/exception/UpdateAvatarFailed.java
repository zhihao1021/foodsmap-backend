package com.nckueat.foodsmap.exception;

import org.springframework.http.HttpStatus;
import com.nckueat.foodsmap.exceptionHandler.HTTPException;

public class UpdateAvatarFailed extends HTTPException {
    public UpdateAvatarFailed() {
        super(HttpStatus.BAD_REQUEST, "Update avatar failed");
    }
}

package com.nckueat.foodsmap.exception;

import org.springframework.http.HttpStatus;
import com.nckueat.foodsmap.exceptionHandler.HTTPException;

public class CFValidateFailed extends HTTPException {
    public CFValidateFailed(String email) {
        super(HttpStatus.BAD_REQUEST, String.format("\"%s\" can't pass cloudflare validate", email));
    }
}

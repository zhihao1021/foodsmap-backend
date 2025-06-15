package com.nckueat.foodsmap.exceptionHandler;

import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.ErrorResponse;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.filter.OncePerRequestFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterExceptionHandler extends OncePerRequestFilter {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) {

        CustomErrorResponse errorResponse;
        try {
            filterChain.doFilter(request, response);
            return;
        } catch (HTTPException e) {
            errorResponse = e.toErrorResponse();
        } catch (Exception e) {
            HttpStatusCode statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
            if (e instanceof ErrorResponse) {
                statusCode = ((ErrorResponse) e).getStatusCode();
            } else if (e instanceof HttpMessageNotReadableException) {
                statusCode = HttpStatus.UNPROCESSABLE_ENTITY;
            } else if (HttpMediaTypeException.class.isAssignableFrom(e.getClass())) {
                statusCode = HttpStatus.UNSUPPORTED_MEDIA_TYPE;
            } else {
                e.printStackTrace();
            }

            errorResponse = new CustomErrorResponse(statusCode, e.getMessage());
        }

        response.setStatus(errorResponse.getStatusCode());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        try {
            objectMapper.writeValue(response.getWriter(), errorResponse);
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

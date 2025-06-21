package com.nckueat.foodsmap.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import com.nckueat.foodsmap.annotation.CurrentUserId;
import com.nckueat.foodsmap.annotation.OptionalCurrentUserId;
import com.nckueat.foodsmap.component.jwt.JwtAuthenticationToken;
import com.nckueat.foodsmap.component.jwt.JwtUtil;
import com.nckueat.foodsmap.exception.Unauthorized;

public class CurrentUserIdArgumentResolver implements HandlerMethodArgumentResolver {
    private final JwtUtil jwtUtil;

    public CurrentUserIdArgumentResolver(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean supportsParameter(@NonNull MethodParameter parameter) {
        return (parameter.hasParameterAnnotation(CurrentUserId.class)
                || parameter.hasParameterAnnotation(OptionalCurrentUserId.class))
                && Long.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Long resolveArgument(@NonNull MethodParameter parameter,
            @Nullable ModelAndViewContainer mavContainer, @NonNull NativeWebRequest webRequest,
            @Nullable WebDataBinderFactory binderFactory) throws Exception {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            if (parameter.hasParameterAnnotation(OptionalCurrentUserId.class)) {
                return null;
            }

            throw new Unauthorized();
        }

        if (authentication instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
            Long userId = jwtAuthenticationToken.getPrincipal();
            String token = jwtAuthenticationToken.getCredentials();

            if (jwtUtil.validateToken(token)) {
                return userId;
            }
        }

        if (parameter.hasParameterAnnotation(OptionalCurrentUserId.class)) {
            return null;
        }
        throw new Unauthorized();
    }
}

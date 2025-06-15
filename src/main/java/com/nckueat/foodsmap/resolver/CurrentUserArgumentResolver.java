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
import com.nckueat.foodsmap.annotation.CurrentUser;
import com.nckueat.foodsmap.component.jwt.JwtAuthenticationToken;
import com.nckueat.foodsmap.component.jwt.JwtUtil;
import com.nckueat.foodsmap.exception.Unauthorized;
import com.nckueat.foodsmap.model.entity.User;
import com.nckueat.foodsmap.service.UserService;

public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public CurrentUserArgumentResolver(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean supportsParameter(@NonNull MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class)
                && (User.class.isAssignableFrom(parameter.getParameterType())
                        || User.class.isAssignableFrom(parameter.getParameterType()));
    }

    @Override
    public User resolveArgument(@NonNull MethodParameter parameter,
            @Nullable ModelAndViewContainer mavContainer, @NonNull NativeWebRequest webRequest,
            @Nullable WebDataBinderFactory binderFactory) throws Exception {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new Unauthorized();
        }

        if (authentication instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
            Long userId = jwtAuthenticationToken.getPrincipal();
            String token = jwtAuthenticationToken.getCredentials();

            if (jwtUtil.validateToken(token)) {
                User user = userService.getUserById(userId);
                return user;
            }
        }

        throw new Unauthorized();
    }
}

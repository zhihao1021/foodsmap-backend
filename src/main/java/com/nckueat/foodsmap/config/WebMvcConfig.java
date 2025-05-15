package com.nckueat.foodsmap.config;

import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.nckueat.foodsmap.component.Jwt.JwtUtil;
import com.nckueat.foodsmap.resolver.CurrentUserArgumentResolver;
import com.nckueat.foodsmap.resolver.CurrentUserIdArgumentResolver;
import com.nckueat.foodsmap.service.UserService;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public WebMvcConfig(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void addArgumentResolvers(@NonNull List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new CurrentUserArgumentResolver(userService, jwtUtil));
        resolvers.add(new CurrentUserIdArgumentResolver(jwtUtil));
    }
}

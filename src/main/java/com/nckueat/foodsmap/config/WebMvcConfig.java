package com.nckueat.foodsmap.config;

import java.util.List;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import com.nckueat.foodsmap.component.jwt.JwtUtil;
import com.nckueat.foodsmap.properties.MediaProperties;
import com.nckueat.foodsmap.resolver.CurrentUserArgumentResolver;
import com.nckueat.foodsmap.resolver.CurrentUserIdArgumentResolver;
import com.nckueat.foodsmap.service.UserService;

@Configuration
@EnableConfigurationProperties(MediaProperties.class)
public class WebMvcConfig implements WebMvcConfigurer {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final String mediaPath;

    public WebMvcConfig(UserService userService, JwtUtil jwtUtil, MediaProperties mediaProperties) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.mediaPath = mediaProperties.getStorePath();
    }

    @Override
    public void addArgumentResolvers(@NonNull List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new CurrentUserArgumentResolver(userService, jwtUtil));
        resolvers.add(new CurrentUserIdArgumentResolver(jwtUtil));
    }

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/media/**").addResourceLocations("file:" + mediaPath)
                .setCachePeriod(3600);
    }
}

package com.nckueat.foodsmap.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.unit.DataSize;
import org.springframework.web.filter.OncePerRequestFilter;
import com.nckueat.foodsmap.exception.ContentLengthRequired;
import com.nckueat.foodsmap.exception.FileSizeLimitExceeded;
import com.nckueat.foodsmap.properties.FileSizeProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@EnableConfigurationProperties(FileSizeProperties.class)
public class FileSizeFilter extends OncePerRequestFilter {
    private long avatarSizeLimit;

    public FileSizeFilter(FileSizeProperties fileSizeProperties) {
        this.avatarSizeLimit = DataSize.parse(fileSizeProperties.getAvatar()).toBytes();
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws java.io.IOException, jakarta.servlet.ServletException {
        String path = request.getRequestURI();
        String method = request.getMethod();

        if (path.startsWith("/avatar") && method.equals("PUT")) {
            long contentLength = request.getContentLengthLong();
            if (contentLength == -1) {
                throw new ContentLengthRequired();
            }

            if (contentLength > avatarSizeLimit) {
                throw new FileSizeLimitExceeded(avatarSizeLimit);
            }

        }

        filterChain.doFilter(request, response);
    }
}

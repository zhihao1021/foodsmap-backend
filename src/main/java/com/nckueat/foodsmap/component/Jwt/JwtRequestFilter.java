package com.nckueat.foodsmap.component.Jwt;

import java.io.IOException;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    public JwtRequestFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);
        if (!token.isEmpty() && jwtUtil.validateToken(token)) {
            final Long userId = jwtUtil.extractUserId(token);

            Authentication authenticationToken = new JwtAuthenticationToken(userId, token, null);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        // else {
        //     final String cookies = request.getHeader("Cookie");
        //     if (cookies != null && cookies.contains("access_token=")) {
        //         // final String jwtCookie = 
        //         final String jwtCookie = cookies.split("access_token=")[1].split(";")[0];
        //         if (!jwtCookie.isEmpty() && jwtUtil.validateToken(jwtCookie)) {
        //             final Long userId = jwtUtil.extractUserId(jwtCookie);

        //             Authentication authenticationToken = new JwtAuthenticationToken(userId, jwtCookie, null);
        //             SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        //         }
        //     }
        // }

        filterChain.doFilter(request, response);
    }
}

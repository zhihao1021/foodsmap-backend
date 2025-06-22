package com.nckueat.foodsmap.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import com.nckueat.foodsmap.component.jwt.JwtRequestFilter;
import com.nckueat.foodsmap.exceptionHandler.FilterExceptionHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtRequestFilter jwtRequestFilter;
    private final FileSizeFilter fileSizeFilter;
    private final FilterExceptionHandler filterExceptionHandler;

    public SecurityConfig(JwtRequestFilter jwtRequestFilter, FileSizeFilter fileSizeFilter,
            FilterExceptionHandler filterExceptionHandler) {
        this.jwtRequestFilter = jwtRequestFilter;
        this.fileSizeFilter = fileSizeFilter;
        this.filterExceptionHandler = filterExceptionHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.requestMatchers("/auth/**", "/avatar/**")
                        // .permitAll().anyRequest().authenticated())
                        .permitAll().anyRequest().permitAll())
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(filterExceptionHandler, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(jwtRequestFilter, FilterExceptionHandler.class)
                .addFilterAfter(fileSizeFilter,  JwtRequestFilter.class)
                .formLogin(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedMethod("*");
        configuration.addAllowedOrigin("http://localhost:3000");
        configuration.addAllowedOrigin("https://www.yssfoodmap.com");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}


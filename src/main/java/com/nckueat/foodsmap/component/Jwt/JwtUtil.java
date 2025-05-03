package com.nckueat.foodsmap.component.Jwt;

import java.security.Key;
import java.util.Calendar;
import java.util.Date;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import com.nckueat.foodsmap.model.enitiy.User;
import com.nckueat.foodsmap.properties.JWTProperties;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;


@Component
@EnableConfigurationProperties(JWTProperties.class)
public class JwtUtil {
    private final Key key;

    public JwtUtil(JWTProperties properties) {
        final String secret = properties.getSecret();
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(User user) {
        return this.generateToken(user, false);
    }

    public String generateToken(User user, boolean noExpiration) {
        Calendar expiredCalendar = Calendar.getInstance();
        if (noExpiration) {
            expiredCalendar.add(Calendar.YEAR, 1); // 1 year
        } else {
            expiredCalendar.add(Calendar.DATE, 7); // 7 days
        }

        return Jwts.builder().setSubject(user.getId().toString()).setIssuedAt(new Date())
                .setExpiration(expiredCalendar.getTime()).signWith(key).compact();
    }

    public Long extractUserId(String token) {
        try {

            final String userId = Jwts.parserBuilder().setSigningKey(key).build()
                    .parseClaimsJws(token).getBody().getSubject();

            try {
                return Long.parseLong(userId);
            } catch (NumberFormatException e) {
                return null;
            }
        } catch (SignatureException e) {
            return null;
        }
    }

    public boolean isTokenExpired(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody()
                .getExpiration().before(new Date());
    }

    public boolean validateToken(String token) {
        final Long userId = this.extractUserId(token);
        return userId != null && !isTokenExpired(token);
    }
}

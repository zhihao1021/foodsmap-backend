package com.nckueat.foodsmap.component.jwt;

import java.security.Key;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import com.nckueat.foodsmap.properties.JWTProperties;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;


@Component
@EnableConfigurationProperties(JWTProperties.class)
public class JwtUtil {
    private final Key key;

    public JwtUtil(JWTProperties properties) {
        final String secret = properties.getSecret();
        this.key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
    }

    public String generateToken(Long userId) {
        return this.generateToken(userId, false);
    }

    public String generateToken(Long userId, boolean noExpiration) {
        Calendar expiredCalendar = Calendar.getInstance();
        if (noExpiration) {
            expiredCalendar.add(Calendar.YEAR, 1); // 1 year
        } else {
            expiredCalendar.add(Calendar.DATE, 7); // 7 days
        }

        return Jwts.builder().setSubject(userId.toString()).setIssuedAt(new Date())
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
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException
                | SignatureException | IllegalArgumentException e) {
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

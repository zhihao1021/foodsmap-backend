package com.nckueat.foodsmap.properties;

import java.util.Base64;
import org.springframework.boot.context.properties.ConfigurationProperties;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Data;

@Data
@ConfigurationProperties(prefix = "jwt")
public class JWTProperties {
    private String secret = Base64.getEncoder()
            .encodeToString(Keys.secretKeyFor(SignatureAlgorithm.HS256).getEncoded());
}

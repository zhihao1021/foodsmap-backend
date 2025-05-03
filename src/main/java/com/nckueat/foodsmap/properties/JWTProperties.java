package com.nckueat.foodsmap.properties;

import java.util.Arrays;

import org.springframework.boot.context.properties.ConfigurationProperties;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Data;

@Data
@ConfigurationProperties(prefix = "jwt")
public class JWTProperties {
    private String secret =
            Arrays.toString(Keys.secretKeyFor(SignatureAlgorithm.HS256).getEncoded());
}

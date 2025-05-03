package com.nckueat.foodsmap.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "cf-turnstile")
public class CloudflareTurnstileProperties {
    private String secret;
    private String apiUrl = "https://challenges.cloudflare.com/turnstile/v0/siteverify";
}

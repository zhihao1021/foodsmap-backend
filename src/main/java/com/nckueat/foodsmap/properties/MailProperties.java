package com.nckueat.foodsmap.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "mail")
public class MailProperties {
    private String from;
    private boolean enabled;
}

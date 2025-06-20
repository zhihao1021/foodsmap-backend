package com.nckueat.foodsmap.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import lombok.Data;

@Data
@ConfigurationProperties(prefix = "media")
public class MediaProperties {
    private String storePath = "media";
}

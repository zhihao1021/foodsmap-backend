package com.nckueat.foodsmap.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import lombok.Data;

@Data
@ConfigurationProperties(prefix = "file-size-limit")
public class FileSizeProperties {
    private String avatar = "5MB";
}

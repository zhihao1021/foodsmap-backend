package com.nckueat.foodsmap.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
@Validated
@ConfigurationProperties(prefix = "snowflake")
public class SnowflakeProperties {
    @Min(0)
    @Max(31)
    private long workerId = 0;

    @Min(0)
    @Max(31)
    private long datacenterId = 0;
}

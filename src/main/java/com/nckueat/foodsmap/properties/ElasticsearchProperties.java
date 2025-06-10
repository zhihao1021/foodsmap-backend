package com.nckueat.foodsmap.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import lombok.Data;

@Data
@ConfigurationProperties(prefix = "elasticsearch")
public class ElasticsearchProperties {
    private String[] uris = {"localhost:9200"};
    private int connectTimeout = -1;
    private int socketTimeout = -1;
    private AuthProperties auth = null;
    private SslProperties ssl = null;

    @Data
    public static class AuthProperties {
        private String apiKey = "";
        private String username = "";
        private String password = "";

    }

    @Data
    public static class SslProperties {
        private boolean enabled = false;
        private String certificate = "";
    }
}

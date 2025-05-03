package com.nckueat.foodsmap.component.CloudflareTurnstile;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nckueat.foodsmap.exception.CFValidateFailed;
import com.nckueat.foodsmap.properties.CloudflareTurnstileProperties;
import net.minidev.json.JSONObject;

@Component
@EnableConfigurationProperties(CloudflareTurnstileProperties.class)
public class CloudflareTurnstile {
    private final String secret;
    private final String apiUrl;
    private HttpClient client;

    public CloudflareTurnstile(CloudflareTurnstileProperties properties) {
        this.secret = properties.getSecret();
        this.apiUrl = properties.getApiUrl();
        this.client = HttpClient.newHttpClient();
    }

    public void verify(String email, String token) throws CFValidateFailed {
        this.verify(email, token, null);
    }

    public void verify(String email, String token, String remoteIp) throws CFValidateFailed {
        HashMap<String, String> data = new HashMap<>();
        data.put("secret", this.secret);
        data.put("response", token);
        if (remoteIp != null) {
            data.put("remoteip", remoteIp);
        }

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(this.apiUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(new JSONObject(data).toString())).build();

        try {
            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());
            HashMap<String, Object> responseData = new ObjectMapper().readValue(response.body(),
                    new TypeReference<HashMap<String, Object>>() {});

            Object success = responseData.get("success");
            if (success instanceof Boolean && (Boolean) success) {
                return;
            } else if (success instanceof String && Boolean.parseBoolean((String) success)) {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new CFValidateFailed(email);
    }
}

package com.nckueat.foodsmap.config;

import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.ClientConfiguration.MaybeSecureClientConfigurationBuilder;
import org.springframework.data.elasticsearch.client.ClientConfiguration.TerminalClientConfigurationBuilder;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.support.HttpHeaders;
import com.nckueat.foodsmap.properties.ElasticsearchProperties;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

@Configuration
@EnableConfigurationProperties(ElasticsearchProperties.class)
public class MyElasticsearchConfig extends ElasticsearchConfiguration {
    private final String[] uris;
    private final int connectTimeout;
    private final int socketTimeout;
    private final String apiKey;
    private final String username;
    private final String password;
    private final boolean useSsl;
    private final String certificate;

    @Autowired
    ResourceLoader resourceLoader;

    public MyElasticsearchConfig(ElasticsearchProperties elasticsearchProperties) {
        super();
        this.uris = elasticsearchProperties.getUris();
        this.connectTimeout = elasticsearchProperties.getConnectTimeout();
        this.socketTimeout = elasticsearchProperties.getSocketTimeout();

        if (elasticsearchProperties.getAuth() != null) {
            this.apiKey = elasticsearchProperties.getAuth().getApiKey();
            this.username = elasticsearchProperties.getAuth().getUsername();
            this.password = elasticsearchProperties.getAuth().getPassword();
        } else {
            this.apiKey = "";
            this.username = "";
            this.password = "";
        }

        if (elasticsearchProperties.getSsl() != null) {
            this.useSsl = elasticsearchProperties.getSsl().isEnabled();
            this.certificate = elasticsearchProperties.getSsl().getCertificate();
        } else {
            this.useSsl = false;
            this.certificate = "";
        }
    }

    @Override
    public ClientConfiguration clientConfiguration() {
        TerminalClientConfigurationBuilder builder =
                ClientConfiguration.builder().connectedTo(uris);

        if (useSsl) {
            try {
                builder = ((MaybeSecureClientConfigurationBuilder) builder)
                        .usingSsl(buildSSLContext());
            } catch (Exception e) {
                throw new RuntimeException("Failed to create SSL context for Elasticsearch", e);
            }
        }

        if (connectTimeout > 0) {
            builder = builder.withConnectTimeout(connectTimeout);
        }

        if (socketTimeout > 0) {
            builder = builder.withSocketTimeout(socketTimeout);
        }

        if (!apiKey.isBlank()) {
            builder = builder.withHeaders(() -> {
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "ApiKey " + apiKey);
                return headers;
            });
        } else if (!username.isBlank() && !password.isBlank()) {
            builder = builder.withBasicAuth(username, password);
        }

        return builder.build();
    }

    private SSLContext buildSSLContext() throws CertificateException, FileNotFoundException,
            IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");

        // Load the CA certificate
        InputStream caInput = resourceLoader.getResource(certificate).getInputStream();
        Certificate ca = cf.generateCertificate(caInput);
        caInput.close();

        // Create a KeyStore containing the trusted CA
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);

        // Create a TrustManager that trusts the CAs in our KeyStore
        TrustManagerFactory tmf =
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(keyStore);

        // Create an SSLContext that uses our TrustManager
        SSLContext sslContext =
                SSLContextBuilder.create().loadTrustMaterial(keyStore, null).build();

        return sslContext;
    }
}

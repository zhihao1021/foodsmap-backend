package com.nckueat.foodsmap.component.nextId;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class NextIdTokenConverter {
    private final byte[] key = new byte[32];
    private final int tagLength = 128;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Encoder base64Encoder = Base64.getEncoder();
    private final Decoder base64Decoder = Base64.getDecoder();
    private final SecureRandom secureRandom;

    public NextIdTokenConverter() throws NoSuchAlgorithmException {
        this.secureRandom = SecureRandom.getInstanceStrong();
        this.secureRandom.nextBytes(key);
    }

    public <T> String getNextToken(@NonNull T nextId) {
        try {
            final byte[] payloadBytes = objectMapper.writeValueAsBytes(Map.of("nextId", nextId));
            final byte[] iv = new byte[12];
            secureRandom.nextBytes(iv);

            final Cipher encryptCipher = Cipher.getInstance("AES/GCM/NoPadding");
            encryptCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"),
                    new GCMParameterSpec(tagLength, iv));

            return String.format("%s.%s",
                    base64Encoder.encodeToString(encryptCipher.doFinal(payloadBytes)),
                    base64Encoder.encodeToString(iv));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
                | InvalidAlgorithmParameterException | JsonProcessingException
                | IllegalBlockSizeException | BadPaddingException e) {
            return null;
        }
    }

    public <T> T parseNextId(@NonNull String token) {
        final String[] parts = token.split("\\.");
        if (parts.length != 2) {
            return null;
        }

        try {
            final byte[] encryptedPayload = base64Decoder.decode(parts[0]);
            final byte[] iv = base64Decoder.decode(parts[1]);

            final Cipher decryptCipher = Cipher.getInstance("AES/GCM/NoPadding");
            decryptCipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"),
                    new GCMParameterSpec(tagLength, iv));
            final Map<String, T> metadata =
                    objectMapper.readValue(decryptCipher.doFinal(encryptedPayload),
                            new TypeReference<HashMap<String, T>>() {});

            return metadata.get("nextId");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
                | InvalidAlgorithmParameterException | IllegalArgumentException | IOException
                | IllegalBlockSizeException | BadPaddingException e) {
            return null;
        }
    }
}

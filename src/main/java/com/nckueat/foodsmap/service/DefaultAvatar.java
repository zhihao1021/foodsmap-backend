package com.nckueat.foodsmap.service;

import java.io.IOException;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

@Service
public class DefaultAvatar {
    private final byte[] defaultAvatar;

    public DefaultAvatar() throws IOException {
        ClassPathResource resource = new ClassPathResource("static/default_avatar.png");
        this.defaultAvatar = StreamUtils.copyToByteArray(resource.getInputStream());
    }

    public ResponseEntity<byte[]> getDefaultAvatar() {
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(this.defaultAvatar);
    }
}

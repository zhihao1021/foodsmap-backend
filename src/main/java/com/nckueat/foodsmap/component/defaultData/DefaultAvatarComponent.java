package com.nckueat.foodsmap.component.defaultData;

import java.io.IOException;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

@Component
public class DefaultAvatarComponent {
    private final byte[] defaultAvatar;

    public DefaultAvatarComponent() throws IOException {
        ClassPathResource resource = new ClassPathResource("static/default_avatar.png");
        this.defaultAvatar = StreamUtils.copyToByteArray(resource.getInputStream());
    }

    public ResponseEntity<byte[]> getDefaultAvatar() {
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(this.defaultAvatar);
    }
}

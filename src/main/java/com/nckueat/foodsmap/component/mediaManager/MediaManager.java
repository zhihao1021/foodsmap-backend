package com.nckueat.foodsmap.component.mediaManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import com.nckueat.foodsmap.component.snowflakeId.SnowflakeIdGenerator;
import com.nckueat.foodsmap.exception.UnsupportedMediaType;
import com.nckueat.foodsmap.properties.MediaProperties;

@Component
@EnableConfigurationProperties(MediaProperties.class)
public class MediaManager {
    private final Tika tika = new Tika();
    private final String storePath;
    @Autowired
    private SnowflakeIdGenerator snowflakeIdGenerator;

    public MediaManager(MediaProperties mediaProperties) {
        storePath = mediaProperties.getStorePath();
    }

    public Long saveArticleMedia(Long articleId, MultipartFile media) {
        try {
            final String originalFilename = media.getOriginalFilename();
            final String mediaType = tika.detect(media.getInputStream());
            if (!mediaType.startsWith("image/") && !mediaType.startsWith("video/")) {
                throw new UnsupportedMediaType(originalFilename);
            }

            final Long fileId = snowflakeIdGenerator.nextId();
            final Path storeDirectory =
                    Paths.get(storePath, "article", articleId.toString()).toAbsolutePath();
            final Path storeFile = storeDirectory.resolve(fileId.toString());

            if (!storeDirectory.toFile().exists()) {
                storeDirectory.toFile().mkdirs();
            }

            File file = storeFile.toFile();
            if (file.exists()) {
                file.delete();
            }

            file.createNewFile();
            media.transferTo(file);
            return fileId;
        } catch (IOException e) {
            return null;
        }

    }
}

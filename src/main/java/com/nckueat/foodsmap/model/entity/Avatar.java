package com.nckueat.foodsmap.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
@Document(collection = "avatars")
public class Avatar {
    @Id
    private Long id;

    @NonNull
    @Indexed(unique = true)
    private Long userId;

    private String contentType;
    private byte[] data;
}

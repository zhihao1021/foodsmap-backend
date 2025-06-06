package com.nckueat.foodsmap.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Document(collection = "avatars")
public class Avatar {
    @Id
    private Long id;

    private String contentType;
    private byte[] data;
}

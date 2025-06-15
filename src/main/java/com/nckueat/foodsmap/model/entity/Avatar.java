package com.nckueat.foodsmap.model.entity;

import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@Table(name = "avatars")
@AllArgsConstructor
@NoArgsConstructor
public class Avatar {
    @Id
    private Long userId;

    private String contentType;
    private byte[] data;
}

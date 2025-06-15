package com.nckueat.foodsmap.model.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ListResponse<T> {
    private final List<T> data;
    private final String token;
}

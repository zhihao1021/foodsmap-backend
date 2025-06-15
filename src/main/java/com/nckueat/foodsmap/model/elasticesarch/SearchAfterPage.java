package com.nckueat.foodsmap.model.elasticesarch;

import java.util.List;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SearchAfterPage<T> extends PageImpl<T> {
    private final List<Object> searchAfterValues;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SearchAfterPage(List<T> content, int size, List<Object> searchAfterValues) {
        super(content, PageRequest.of(0, size), content.size());
        this.searchAfterValues = searchAfterValues;
    }

    public List<Object> getSearchAfterValues() {
        return searchAfterValues;
    }

    public String getSearchAfterTag() {
        try {
            return objectMapper.writeValueAsString(searchAfterValues);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}

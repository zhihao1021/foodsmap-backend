package com.nckueat.foodsmap.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.nckueat.foodsmap.service.SearchService;

@RestController
@RequestMapping("/search")
public class SearchController {
    @Autowired
    private SearchService searchService;

    @GetMapping("popular-tags")
    public ResponseEntity<List<String>> getPopularTags(
            @RequestParam(defaultValue = "10000") int limit) {
        List<String> popularTags = searchService.getPopularTags(limit);

        return ResponseEntity.ok(popularTags);
    }
}

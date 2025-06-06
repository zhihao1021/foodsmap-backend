package com.nckueat.foodsmap.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.nckueat.foodsmap.service.ArticleService;
import com.nckueat.foodsmap.service.UserService;

@RestController
@RequestMapping("/search")
public class SearchController {
    @Autowired
    private UserService userService;
    @Autowired
    private ArticleService articleService;

    
}

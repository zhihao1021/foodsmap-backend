package com.nckueat.foodsmap.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.nckueat.foodsmap.annotation.CurrentUser;
import com.nckueat.foodsmap.model.dto.vo.UserRead;
import com.nckueat.foodsmap.model.enitiy.User;
import com.nckueat.foodsmap.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("")
    public ResponseEntity<UserRead> getCurrentUser(@CurrentUser User user) {
        return ResponseEntity.ok(UserRead.fromUser(user));
    };
}

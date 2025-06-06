package com.nckueat.foodsmap.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nckueat.foodsmap.annotation.CurrentUser;
import com.nckueat.foodsmap.model.dto.request.UserUpdate;
import com.nckueat.foodsmap.model.dto.vo.UserRead;
import com.nckueat.foodsmap.model.dto.vo.ArticleRead;
import com.nckueat.foodsmap.model.dto.vo.GlobalUserView;
import com.nckueat.foodsmap.model.entity.User;
import com.nckueat.foodsmap.service.DefaultAvatar;
import com.nckueat.foodsmap.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private DefaultAvatar defaultAvatar;

    @GetMapping("")
    public ResponseEntity<UserRead> getCurrentUser(@CurrentUser User user) {
        return ResponseEntity.ok(user.toUserRead());
    };

    @PutMapping("")
    public ResponseEntity<UserRead> updateCurrentUser(@CurrentUser User user,
            @NonNull @RequestBody UserUpdate userUpdate) {
        User updatedUser = userService.updateUser(user, userUpdate);

        return ResponseEntity.ok(updatedUser.toUserRead());
    }

    @GetMapping("{userId}")
    public ResponseEntity<GlobalUserView> getCurrentUser(@NonNull @PathVariable Long userId) {
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(user.toGlobalUserView());
    };

    // @GetMapping("avatar")
    // public ResponseEntity<byte[]> getCurrentUserAvatar(@CurrentUser User user) {

    // return ResponseEntity.ok(avatar);
    // }

    @PostMapping("{userID}/articles")
    public ResponseEntity<List<ArticleRead>> getUserArticleById(@PathVariable Long userID) {
        List<ArticleRead> results = userService.findArticleById(userID);
        return ResponseEntity.ok(results);
    }
}

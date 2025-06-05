package com.nckueat.foodsmap.controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.nckueat.foodsmap.annotation.CurrentUser;
import com.nckueat.foodsmap.exception.AvatarNotFound;
import com.nckueat.foodsmap.exception.UpdateAvatarFailed;
import com.nckueat.foodsmap.model.dto.request.UserUpdate;
import com.nckueat.foodsmap.model.dto.vo.UserRead;
import com.nckueat.foodsmap.model.dto.vo.ArticleRead;
import com.nckueat.foodsmap.model.entity.Avatar;
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

    @GetMapping("avatar")
    public ResponseEntity<byte[]> getCurrentUserAvatar(@CurrentUser User user) {
        return getUserAvatar(user.getId());
    }

    @GetMapping("avatar/{userId}")
    public ResponseEntity<byte[]> getUserAvatar(@NonNull @PathVariable Long userId) {
        try {
            Avatar avatar = userService.getAvatar(userId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(avatar.getContentType()));
            headers.setContentLength(avatar.getData().length);

            return ResponseEntity.ok().headers(headers).body(avatar.getData());
        } catch (AvatarNotFound e) {
            return defaultAvatar.getDefaultAvatar();
        }
    }

    @PutMapping(params = "avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE + ";max-size=1MB")
    public ResponseEntity<Void> updateCurrentUserAvatar(@CurrentUser User user,
            @NonNull @RequestParam(name = "file") MultipartFile file) {
        try (InputStream input = file.getInputStream()) {
            if (ImageIO.read(input) == null) {
                throw new UpdateAvatarFailed();
            }

            userService.updateAvatar(user, file.getContentType(), file.getBytes());
        } catch (IOException e) {
            throw new UpdateAvatarFailed();
        }

        URI location = URI.create("/user/avatar");
        return ResponseEntity.created(location).build();
    }

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

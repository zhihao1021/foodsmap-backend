package com.nckueat.foodsmap.controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import javax.imageio.ImageIO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.nckueat.foodsmap.annotation.CurrentUser;
import com.nckueat.foodsmap.exception.AvatarNotFound;
import com.nckueat.foodsmap.exception.UpdateAvatarFailed;
import com.nckueat.foodsmap.model.entity.Avatar;
import com.nckueat.foodsmap.model.entity.User;
import com.nckueat.foodsmap.service.DefaultAvatar;
import com.nckueat.foodsmap.service.UserService;

@RestController
@RequestMapping("/avatar")
public class AvatarController {
    @Autowired
    private UserService userService;
    @Autowired
    private DefaultAvatar defaultAvatar;

    @GetMapping("")
    public ResponseEntity<byte[]> getCurrentUserAvatar(@CurrentUser User user) {
        return getUserAvatar(user.getId());
    }

    @GetMapping("{userId}")
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

    @PutMapping(params = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE + ";max-size=5MB")
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

    @DeleteMapping("")
    public ResponseEntity<Void> deleteCurrentUserAvatar(@CurrentUser User user) {
        userService.deleteAvatar(user);
        return ResponseEntity.noContent().build();
    }
}

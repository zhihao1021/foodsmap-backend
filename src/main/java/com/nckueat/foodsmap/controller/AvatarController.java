package com.nckueat.foodsmap.controller;

import java.net.URI;
import java.util.function.Supplier;
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
import com.nckueat.foodsmap.component.defaultData.DefaultAvatarComponent;
import com.nckueat.foodsmap.exception.AvatarNotFound;
import com.nckueat.foodsmap.model.entity.Avatar;
import com.nckueat.foodsmap.model.entity.User;
import com.nckueat.foodsmap.service.UserService;

@RestController
@RequestMapping("/avatar")
public class AvatarController {
    @Autowired
    private UserService userService;
    @Autowired
    private DefaultAvatarComponent defaultAvatar;

    private ResponseEntity<byte[]> generateAvatarResponseEntity(Supplier<Avatar> avatarSupplier) {
        try {
            Avatar avatar = avatarSupplier.get();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("image/webp"));
            headers.setContentLength(avatar.getData().length);

            return ResponseEntity.ok().headers(headers).body(avatar.getData());
        } catch (AvatarNotFound e) {
            return defaultAvatar.getDefaultAvatar();
        }
    }

    @GetMapping("")
    public ResponseEntity<byte[]> getCurrentUserAvatar(@CurrentUser User user) {
        return getUserAvatarById(user.getId());
    }

    @GetMapping("by-id/{userId}")
    public ResponseEntity<byte[]> getUserAvatarById(@NonNull @PathVariable Long userId) {
        return generateAvatarResponseEntity(() -> userService.getAvatarById(userId));
    }

    @GetMapping("by-username/{username}")
    public ResponseEntity<byte[]> getUserAvatarByUsername(@NonNull @PathVariable String username) {
        return generateAvatarResponseEntity(() -> userService.getAvatarByUsername(username));
    }

    @PutMapping(path = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateCurrentUserAvatar(@CurrentUser User user,
            @NonNull @RequestParam(name = "file") MultipartFile file) {

        userService.updateAvatar(user, file);

        URI location = URI.create("/user/avatar");
        return ResponseEntity.created(location).build();
    }

    @DeleteMapping("")
    public ResponseEntity<Void> deleteCurrentUserAvatar(@CurrentUser User user) {
        userService.deleteAvatar(user);
        return ResponseEntity.noContent().build();
    }
}

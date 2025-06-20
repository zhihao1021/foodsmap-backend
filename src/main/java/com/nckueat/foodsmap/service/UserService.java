package com.nckueat.foodsmap.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.luciad.imageio.webp.WebPWriteParam;
import com.nckueat.foodsmap.Utils.PasswordChecker;
import com.nckueat.foodsmap.component.emailValidation.EmailValidation;
import com.nckueat.foodsmap.exception.AvatarNotFound;
import com.nckueat.foodsmap.exception.DisplayNameTooLong;
import com.nckueat.foodsmap.exception.DisplayNameTooShort;
import com.nckueat.foodsmap.exception.EmailValidateTooManyRetry;
import com.nckueat.foodsmap.exception.PasswordNotMatch;
import com.nckueat.foodsmap.exception.PasswordTooWeak;
import com.nckueat.foodsmap.exception.UpdateAvatarFailed;
import com.nckueat.foodsmap.exception.UserNotFound;
import com.nckueat.foodsmap.exception.WrongValidateCode;
import com.nckueat.foodsmap.model.dto.request.UserUpdate;
import com.nckueat.foodsmap.model.entity.Avatar;
import com.nckueat.foodsmap.model.entity.User;
import com.nckueat.foodsmap.repository.postgresql.AvatarRepository;
import com.nckueat.foodsmap.repository.postgresql.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AvatarRepository avatarRepository;
    @Autowired
    private EmailValidation emailValidation;

    public User getUserById(@NonNull Long id) throws UserNotFound {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFound(id.toString()));
        return user;
    }

    public User getUserByUsername(@NonNull String username) throws UserNotFound {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFound(username));
        return user;
    }

    public User updateUser(@NonNull User user, @NonNull UserUpdate userUpdate)
            throws WrongValidateCode, EmailValidateTooManyRetry, DisplayNameTooLong,
            DisplayNameTooShort, PasswordNotMatch, PasswordTooWeak {
        userUpdate.getEmail().ifPresent(email -> {
            emailValidation.validateEmail(user.getEmail(), userUpdate.getOldEmailValidCode().get(),
                    userUpdate.getOldIdentifyCode().get());
            emailValidation.validateEmail(email, userUpdate.getEmailValidCode().get(),
                    userUpdate.getIdentifyCode().get());

            user.setEmail(email);
        });

        userUpdate.getDisplayName().ifPresent(displayName -> {
            if (displayName.length() < 1) {
                throw new DisplayNameTooShort();
            }
            if (displayName.length() > 64) {
                throw new DisplayNameTooLong();
            }
            user.setDisplayName(displayName);
        });

        userUpdate.getNewPassword().ifPresent(newPassword -> {
            if (!user.checkPassword(userUpdate.getPassword().get())) {
                throw new PasswordNotMatch(user.getUsername());
            }

            if (!PasswordChecker.check(newPassword)) {
                throw new PasswordTooWeak();
            }

            user.setPassword(newPassword);
        });

        return userRepository.save(user);
    }

    public Avatar getAvatarById(@NonNull Long userId) throws AvatarNotFound {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFound(userId.toString());
        }

        return avatarRepository.findById(userId).orElseThrow(AvatarNotFound::new);
    }

    public Avatar getAvatarByUsername(@NonNull String username) throws AvatarNotFound {
        Long userId = userRepository.findIdByUsername(username)
                .orElseThrow(() -> new UserNotFound(username));

        return avatarRepository.findById(userId).orElseThrow(AvatarNotFound::new);
    }

    public void updateAvatar(@NonNull User user, @NonNull MultipartFile file)
            throws UpdateAvatarFailed {
        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null) {
                throw new UpdateAvatarFailed();
            }

            // Convert the image to WebP format
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(outputStream);
            ImageWriter writer = ImageIO.getImageWritersByMIMEType("image/webp").next();
            writer.setOutput(imageOutputStream);

            WebPWriteParam writeParam = new WebPWriteParam(writer.getLocale());
            writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            writeParam.setCompressionType(
                    writeParam.getCompressionTypes()[WebPWriteParam.LOSSY_COMPRESSION]);
            writeParam.setCompressionQuality(0.6f);

            writer.write(null, new IIOImage(image, null, null), writeParam);
            imageOutputStream.close();

            Avatar avatar =
                    Avatar.builder().userId(user.getId()).data(outputStream.toByteArray()).build();
            avatarRepository.save(avatar);
        } catch (IOException e) {
            throw new UpdateAvatarFailed();
        }

    }

    public void deleteAvatar(@NonNull User user) {
        avatarRepository.deleteById(user.getId());
    }
}

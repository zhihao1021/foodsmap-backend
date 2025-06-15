package com.nckueat.foodsmap.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
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

    public void updateAvatar(@NonNull User user, String contentType, @NonNull byte[] data)
            throws UpdateAvatarFailed {

        Avatar avatar = Avatar.builder().userId(user.getId()).contentType(contentType).data(data).build();
        avatarRepository.save(avatar);
    }

    public void deleteAvatar(@NonNull User user) {
        avatarRepository.deleteById(user.getId());
    }
}

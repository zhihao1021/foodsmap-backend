package com.nckueat.foodsmap.service;

import java.io.InputStream;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import com.nckueat.foodsmap.Utils.PasswordChecker;
import com.nckueat.foodsmap.component.EmailValidation.EmailValidation;
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
import com.nckueat.foodsmap.repository.AvatarRepository;
import com.nckueat.foodsmap.repository.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AvatarRepository avatarRepository;
    @Autowired
    private EmailValidation emailValidation;
    @Autowired
    private MongoTemplate mongoTemplate;

    public User getUserById(@NonNull Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFound(id.toString()));
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

    public Avatar getAvatar(@NonNull Long userId) throws AvatarNotFound {
        return avatarRepository.findByUserId(userId).orElseThrow(AvatarNotFound::new);
    }

    public void updateAvatar(@NonNull User user, String contentType,
            @NonNull byte[] data) throws UpdateAvatarFailed {

        Query query = new Query(Criteria.where("userId").is(user.getId()));
        Update update = new Update().set("contentType", contentType).set("data", data);

        mongoTemplate.upsert(query, update, Avatar.class);
    }
}

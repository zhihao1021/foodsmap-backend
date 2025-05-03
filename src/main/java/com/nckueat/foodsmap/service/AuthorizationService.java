package com.nckueat.foodsmap.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.mongodb.DuplicateKeyException;
import com.nckueat.foodsmap.component.CloudflareTurnstile.CloudflareTurnstile;
import com.nckueat.foodsmap.component.EmailValidation.EmailValidation;
import com.nckueat.foodsmap.component.Jwt.JwtUtil;
import com.nckueat.foodsmap.component.SnowflakeId.SnowflakeIdGenerator;
import com.nckueat.foodsmap.exception.CFValidateFailed;
import com.nckueat.foodsmap.exception.EmailNotRegisted;
import com.nckueat.foodsmap.exception.PasswordNotMatch;
import com.nckueat.foodsmap.exception.TooFrequentResends;
import com.nckueat.foodsmap.exception.UserAlreadyExist;
import com.nckueat.foodsmap.exception.UserNotFound;
import com.nckueat.foodsmap.exception.UsernameIllegal;
import com.nckueat.foodsmap.exception.WrongValidateCode;
import com.nckueat.foodsmap.model.dto.Jwt;
import com.nckueat.foodsmap.model.dto.request.UserCreate;
import com.nckueat.foodsmap.model.enitiy.User;
import com.nckueat.foodsmap.repository.UserRepository;
import com.nckueat.foodsmap.types.LoginMethod;

@Service
public class AuthorizationService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private SnowflakeIdGenerator snowflakeIdGenerator;
    @Autowired
    private CloudflareTurnstile cloudflareTurnstile;
    @Autowired
    private EmailValidation emailValidation;

    public List<LoginMethod> getLoginMethods(String emailOrUsername)
            throws EmailNotRegisted, UserNotFound {
        EmailValidator emailValidator = EmailValidator.getInstance();

        User user;
        if (emailValidator.isValid(emailOrUsername)) {
            user = userRepository.findByEmail(emailOrUsername)
                    .orElseThrow(() -> new EmailNotRegisted(emailOrUsername));
        } else {
            user = userRepository.findByUsername(emailOrUsername)
                    .orElseThrow(() -> new UserNotFound(emailOrUsername));
        }

        List<LoginMethod> results = new ArrayList<>();

        results.add(LoginMethod.PASSWORD);
        if (user.getTotpSecret() != null)
            results.add(LoginMethod.TOTP);

        return results;
    }

    public void checkUsername(String username) throws UsernameIllegal, UserAlreadyExist {
        if (!username.matches("^[a-zA-Z0-9_]{5,30}$")) {
            throw new UsernameIllegal();
        }

        if (userRepository.existsByUsername(username)) {
            throw new UserAlreadyExist(username);
        }
    }

    public String sendValidateEmail(String email, String cfResponse)
            throws UserAlreadyExist, CFValidateFailed, TooFrequentResends {
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExist(email);
        }

        cloudflareTurnstile.verify(email, cfResponse);

        return emailValidation.sendValidateEmail(email);
    }

    public void checkEmail(String email, String code, String identifyCode)
            throws WrongValidateCode {
        emailValidation.preCheck(email, code, identifyCode);
    }

    public Jwt register(UserCreate userCreate)
            throws UserAlreadyExist, UsernameIllegal, WrongValidateCode {
        if (!userCreate.getUsername().matches("^[a-zA-Z0-9_]{5,30}$")) {
            throw new UsernameIllegal();
        }
        if (userRepository.existsByUsername(userCreate.getUsername())) {
            throw new UserAlreadyExist(userCreate.getUsername());
        }
        if (userRepository.existsByEmail(userCreate.getEmail())) {
            throw new UserAlreadyExist(userCreate.getEmail());
        }

        emailValidation.validateEmail(userCreate.getEmail(), userCreate.getEmailValidCode(),
                userCreate.getIdentifyCode());

        try {
            User user = userRepository
                    .save(User.fromUserCreate(snowflakeIdGenerator.nextId(), userCreate));
            String token = jwtUtil.generateToken(user, userCreate.isNoExpiration());
            return Jwt.builder().access_token(token).build();
        } catch (DuplicateKeyException e) {
            throw new UserAlreadyExist(userCreate.getUsername());
        }
    }

    public Jwt loginByPassword(String emailOrUsername, String password, boolean noExpiration)
            throws UserNotFound, PasswordNotMatch {
        EmailValidator emailValidator = EmailValidator.getInstance();

        User user;
        if (emailValidator.isValid(emailOrUsername)) {
            user = userRepository.findByEmail(emailOrUsername)
                    .orElseThrow(() -> new UserNotFound(emailOrUsername));
        } else {
            user = userRepository.findByUsername(emailOrUsername)
                    .orElseThrow(() -> new UserNotFound(emailOrUsername));
        }

        if (!user.checkPassword(password)) {
            throw new PasswordNotMatch(emailOrUsername);
        }

        String token = jwtUtil.generateToken(user, noExpiration);
        return Jwt.builder().access_token(token).build();
    }
}

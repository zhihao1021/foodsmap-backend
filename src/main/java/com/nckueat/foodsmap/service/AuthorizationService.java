package com.nckueat.foodsmap.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import com.nckueat.foodsmap.Utils.PasswordChecker;
import com.nckueat.foodsmap.component.cloudflareTurnstile.CloudflareTurnstile;
import com.nckueat.foodsmap.component.emailValidation.EmailValidation;
import com.nckueat.foodsmap.component.jwt.JwtUtil;
import com.nckueat.foodsmap.component.snowflakeId.SnowflakeIdGenerator;
import com.nckueat.foodsmap.exception.CFValidateFailed;
import com.nckueat.foodsmap.exception.DisplayNameTooLong;
import com.nckueat.foodsmap.exception.DisplayNameTooShort;
import com.nckueat.foodsmap.exception.EmailNotRegisted;
import com.nckueat.foodsmap.exception.EmailValidateTooManyRetry;
import com.nckueat.foodsmap.exception.PasswordNotMatch;
import com.nckueat.foodsmap.exception.PasswordTooWeak;
import com.nckueat.foodsmap.exception.TooFrequentResends;
import com.nckueat.foodsmap.exception.UserAlreadyExist;
import com.nckueat.foodsmap.exception.UserNotFound;
import com.nckueat.foodsmap.exception.UsernameIllegal;
import com.nckueat.foodsmap.exception.WrongEmailFormat;
import com.nckueat.foodsmap.exception.WrongValidateCode;
import com.nckueat.foodsmap.model.dto.Jwt;
import com.nckueat.foodsmap.model.dto.request.UserCreate;
import com.nckueat.foodsmap.model.entity.User;
import com.nckueat.foodsmap.repository.postgresql.UserRepository;
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

    public List<LoginMethod> getLoginMethods(@NonNull String emailOrUsername)
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

    public void checkUsername(@NonNull String username) throws UsernameIllegal, UserAlreadyExist {
        if (!username.matches("^[a-zA-Z0-9_]{5,30}$")) {
            throw new UsernameIllegal();
        }

        if (userRepository.existsByUsername(username)) {
            throw new UserAlreadyExist(username);
        }
    }

    public void checkEmail(@NonNull String email) throws WrongEmailFormat, UserAlreadyExist {
        EmailValidator emailValidator = EmailValidator.getInstance();
        if (!emailValidator.isValid(email)) {
            throw new WrongEmailFormat();
        }

        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExist(email);
        }
    }

    public String sendValidateEmail(@NonNull String email, @NonNull String cfResponse)
            throws WrongEmailFormat, UserAlreadyExist, CFValidateFailed, TooFrequentResends {
        return sendValidateEmail(email, cfResponse, true);
    }

    public String sendValidateEmail(@NonNull String email, @NonNull String cfResponse,
            boolean checkExist)
            throws WrongEmailFormat, UserAlreadyExist, CFValidateFailed, TooFrequentResends {
        EmailValidator emailValidator = EmailValidator.getInstance();
        if (!emailValidator.isValid(email)) {
            throw new WrongEmailFormat();
        }

        if (checkExist && userRepository.existsByEmail(email)) {
            throw new UserAlreadyExist(email);
        }

        cloudflareTurnstile.verify(email, cfResponse);

        return emailValidation.sendValidateEmail(email);
    }

    public void preCheckEmail(@NonNull String email, @NonNull String code,
            @NonNull String identifyCode) throws WrongValidateCode, EmailValidateTooManyRetry {
        emailValidation.preCheck(email, code, identifyCode);
    }

    public Jwt register(@NonNull UserCreate userCreate)
            throws UserAlreadyExist, UsernameIllegal, WrongValidateCode, WrongEmailFormat,
            EmailValidateTooManyRetry, DisplayNameTooLong, DisplayNameTooShort {
        if (userCreate.getDisplayName().length() < 1) {
            throw new DisplayNameTooShort();
        }
        if (userCreate.getDisplayName().length() > 64) {
            throw new DisplayNameTooLong();
        }
        EmailValidator emailValidator = EmailValidator.getInstance();
        if (!emailValidator.isValid(userCreate.getEmail())) {
            throw new WrongEmailFormat();
        }
        if (!userCreate.getUsername().matches("^[a-zA-Z0-9_]{5,30}$")) {
            throw new UsernameIllegal();
        }
        if (!PasswordChecker.check(userCreate.getPassword())) {
            throw new PasswordTooWeak();
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
            User user = userRepository.save(User.fromUserCreate(snowflakeIdGenerator.nextId(), userCreate));
            String token = jwtUtil.generateToken(user.getId(), userCreate.isNoExpiration());
            return Jwt.builder().access_token(token).build();
        } catch (DataIntegrityViolationException e) {
            throw new UserAlreadyExist(userCreate.getUsername());
        }
    }

    public Jwt loginByPassword(@NonNull String emailOrUsername, @NonNull String password,
            boolean noExpiration) throws UserNotFound, PasswordNotMatch {
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

        String token = jwtUtil.generateToken(user.getId(), noExpiration);
        return Jwt.builder().access_token(token).build();
    }

    public Jwt refreshToken(@NonNull User user) {
        String token = jwtUtil.generateToken(user.getId(), false);
        return Jwt.builder().access_token(token).build();
    }
}

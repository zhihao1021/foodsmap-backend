package com.nckueat.foodsmap.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.nckueat.foodsmap.exception.UnknownLoginMethod;
import com.nckueat.foodsmap.model.dto.Jwt;
import com.nckueat.foodsmap.model.dto.request.SendEmailRequest;
import com.nckueat.foodsmap.model.dto.request.CheckEmailValidateCodeRequest;
import com.nckueat.foodsmap.model.dto.request.CheckUsernameRequest;
import com.nckueat.foodsmap.model.dto.request.LoginMethodRequest;
import com.nckueat.foodsmap.model.dto.request.LoginRequest;
import com.nckueat.foodsmap.model.dto.request.UserCreate;
import com.nckueat.foodsmap.model.dto.response.LoginMethodsResponse;
import com.nckueat.foodsmap.service.AuthorizationService;
import com.nckueat.foodsmap.types.LoginMethod;

@RestController
@RequestMapping("/auth")
public class AuthorizationController {
    @Autowired
    private AuthorizationService authorizationService;


    @PostMapping("login-methods")
    public ResponseEntity<LoginMethodsResponse> getLoginMethods(
            @RequestBody LoginMethodRequest request) {
        final String emailOrUsername = request.getData();

        return ResponseEntity.ok(
                new LoginMethodsResponse(authorizationService.getLoginMethods(emailOrUsername)));
    }

    @PostMapping("check-username")
    public ResponseEntity<Void> checkUsername(@RequestBody CheckUsernameRequest request) {
        final String username = request.getUsername();
        authorizationService.checkUsername(username);

        return ResponseEntity.ok().build();
    }

    @PostMapping("validate-email")
    public ResponseEntity<String> sendValidateEmail(@RequestBody SendEmailRequest request) {
        return ResponseEntity
                .ok(authorizationService.sendValidateEmail(request.getEmail(), request.getCode()));
    }

    @PostMapping("precheck-email")
    public ResponseEntity<Void> checkEmail(@RequestBody CheckEmailValidateCodeRequest request) {
        authorizationService.checkEmail(request.getEmail(), request.getCode(),
                request.getIdentifyCode());

        return ResponseEntity.ok().build();
    }

    @PostMapping("register")
    public ResponseEntity<Jwt> register(@RequestBody UserCreate data) {
        URI location = URI.create("/user");
        return ResponseEntity.created(location).body(authorizationService.register(data));
    }

    @PostMapping("login")
    public ResponseEntity<Jwt> login(@RequestBody LoginRequest data) {
        URI location = URI.create("/user");

        switch (data.getMethod()) {
            case LoginMethod.PASSWORD:
                return ResponseEntity.created(location)
                        .body(authorizationService.loginByPassword(data.getEmailOrUsername(),
                                data.getCode(), data.getNoExpiration().orElse(false)));
            default:
                throw new UnknownLoginMethod(data.getMethod().toString());
        }
    }
}

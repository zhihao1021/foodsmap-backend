package com.nckueat.foodsmap.types;

import com.fasterxml.jackson.annotation.JsonValue;

public enum LoginMethod {
    PASSWORD("password"), TOTP("totp");

    private String method;

    LoginMethod(String method) {
        this.method = method.toLowerCase();
    }

    @JsonValue
    public String getMethod() {
        return method;
    }

    public boolean equals(LoginMethod obj) {
        return obj.getMethod() == this.getMethod();
    }

    public static LoginMethod fromString(String method) {
        for (LoginMethod loginMethod : LoginMethod.values()) {
            if (loginMethod.method.equalsIgnoreCase(method)) {
                return loginMethod;
            }
        }
        throw new IllegalArgumentException("Invalid login method: " + method);
    }
}

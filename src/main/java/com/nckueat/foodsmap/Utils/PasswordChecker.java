package com.nckueat.foodsmap.Utils;

import java.util.regex.Pattern;

public class PasswordChecker {
    public static Pattern regex =
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*[\\d])(?=.*[^A-Za-z0-9]).{8,}$");

    public static boolean check(String password) {
        return regex.matcher(password).matches();
    }
}

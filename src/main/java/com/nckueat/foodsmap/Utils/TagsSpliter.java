package com.nckueat.foodsmap.Utils;

import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public class TagsSpliter {
    private static Pattern matcher = Pattern.compile("(?<=#)([^#\\s]+)");

    public static List<String> spilt(String inputTags) {
        return TagsSpliter.matcher.matcher(inputTags).results().map(MatchResult::group).toList();
    }
}

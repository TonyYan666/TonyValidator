package com.github.tonyyan.validator.utils;

/**
 * Created by yanzhichao on 16/11/2017.
 */
public class StringUtils {

    public static boolean isEmpty(String string) {
        if (string == null) {
            return true;
        }
        if (string.length() <= 0) {
            return true;
        }
        return false;
    }
}

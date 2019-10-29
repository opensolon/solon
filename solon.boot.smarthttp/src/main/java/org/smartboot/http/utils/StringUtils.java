package org.smartboot.http.utils;

public class StringUtils {
    public static boolean equals(String str1, String str2) {
        return str1 == null ? str2 == null : str1.equals(str2);
    }
}

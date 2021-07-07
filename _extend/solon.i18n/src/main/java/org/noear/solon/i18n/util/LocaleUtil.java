package org.noear.solon.i18n.util;

import java.util.Locale;

/**
 * 地区转换工具
 *
 * <pre><code>
 * LocaleUtils.toLocale("zh") == new Locale("zh", "")
 * LocaleUtils.toLocale("zh-Hant") == new Locale("zh", "Hant")
 * LocaleUtils.toLocale("zh-Hant-TW") == new Locale("zh", "Hant", "TW")
 * </code></pre>
 *
 * @author noear
 * @since 1.5
 */
public class LocaleUtil {
    /**
     * 将字符串转换成地区
     *
     * @param str 字符串
     */
    public static Locale toLocale(String str) {
        if (str == null) {
            return null;
        }
        int len = str.length();
        if (len != 2 && len != 5 && len < 7) {
            throw new IllegalArgumentException("Invalid locale format: " + str);
        }
        char ch0 = str.charAt(0);
        char ch1 = str.charAt(1);
        if (ch0 < 'a' || ch0 > 'z' || ch1 < 'a' || ch1 > 'z') {
            throw new IllegalArgumentException("Invalid locale format: " + str);
        }

        if (len == 2) {
            return new Locale(str, "");
        } else {
            if (str.charAt(2) != '_') {
                throw new IllegalArgumentException("Invalid locale format: " + str);
            }
            char ch3 = str.charAt(3);
            if (ch3 == '_') {
                return new Locale(str.substring(0, 2), "", str.substring(4));
            }
            char ch4 = str.charAt(4);
            if (ch3 < 'A' || ch3 > 'Z' || ch4 < 'A' || ch4 > 'Z') {
                throw new IllegalArgumentException("Invalid locale format: " + str);
            }
            if (len == 5) {
                return new Locale(str.substring(0, 2), str.substring(3, 5));
            } else {
                if (str.charAt(5) != '_') {
                    throw new IllegalArgumentException("Invalid locale format: " + str);
                }
                return new Locale(str.substring(0, 2), str.substring(3, 5), str.substring(6));
            }
        }
    }
}

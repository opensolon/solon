package org.noear.nami.common;

/**
 * @author noear 2021/1/19 created
 */
public class TextUtils {
    /**
     * 检查字符串是否为空
     */
    public static boolean isEmpty(String s) {
        return s == null || s.length() == 0;
    }

    /**
     * 检查字符串是否为非空
     */
    public static boolean isNotEmpty(String s) {
        return !isEmpty(s);
    }
}

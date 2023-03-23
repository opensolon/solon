package org.noear.nami.common;

/**
 * 文本工具类
 *
 * @author noear
 * @since 1.2
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

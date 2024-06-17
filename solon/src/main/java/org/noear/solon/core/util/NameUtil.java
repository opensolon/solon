package org.noear.solon.core.util;

/**
 * 名字工具
 *
 * @author noear
 * @since 2.8
 */
public class NameUtil {
    /**
     * 根据字段名获取 getter 名
     */
    public static String getPropGetterName(String fieldName) {
        String firstUpper = fieldName.substring(0, 1).toUpperCase();
        return "get" + firstUpper + fieldName.substring(1);
    }

    /**
     * 根据字段名获取 setter 名
     */
    public static String getPropSetterName(String fieldName) {
        String firstUpper = fieldName.substring(0, 1).toUpperCase();
        return "set" + firstUpper + fieldName.substring(1);
    }

    /**
     * 根据属性名获取守段名
     */
    public static String getFieldName(String propName) {
        String fieldName = propName.substring(3);
        String firstLower = fieldName.substring(0, 1).toLowerCase();
        return firstLower + fieldName.substring(1);
    }
}
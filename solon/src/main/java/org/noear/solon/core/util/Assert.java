/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.core.util;

import org.noear.solon.lang.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Properties;

/**
 * 断言类
 *
 * @author noear
 * @since 3.1
 */
public final class Assert {

    /**
     * 检查字符串是否为空
     *
     * @param s 字符串
     */
    public static boolean isEmpty(String s) {
        return s == null || s.length() == 0;
    }

    /**
     * 检查集合是否为空
     *
     * @param s 集合
     */
    public static boolean isEmpty(Collection s) {
        return s == null || s.size() == 0;
    }

    /**
     * 检查映射是否为空
     *
     * @param s 集合
     */
    public static boolean isEmpty(Map s) {
        return s == null || s.size() == 0;
    }

    /**
     * 检查多值映射是否为空
     *
     * @param s 集合
     */
    public static boolean isEmpty(MultiMap s) {
        return s == null || s.size() == 0;
    }

    /**
     * 检查数组是否为空
     *
     * @param s 集合
     */
    public static <T> boolean isEmpty(T[] s) {
        return s == null || s.length == 0;
    }

    /**
     * 检查属性是否为空
     *
     * @param s 属性
     */
    public static <T> boolean isEmpty(Properties s) {
        return s == null || s.size() == 0;
    }


    /**
     * 检查字符串是否为非空
     *
     * @param s 字符串
     */
    public static boolean isNotEmpty(String s) {
        return !isEmpty(s);
    }

    /**
     * 检查集合是否非空
     *
     * @param s 集合
     */
    public static boolean isNotEmpty(Collection s) {
        return !isEmpty(s);
    }

    /**
     * 检查集合是否非空
     *
     * @param s 集合
     */
    public static <T> boolean isNotEmpty(T[] s) {
        return !isEmpty(s);
    }

    /**
     * 检查集合是否非空
     *
     * @param s 集合
     */
    public static boolean isNotEmpty(Map s) {
        return !isEmpty(s);
    }

    /**
     * 检查属性是否非空
     *
     * @param s 属性
     */
    public static boolean isNotEmpty(Properties s) {
        return !isEmpty(s);
    }


    /**
     * 检查字符串是否为空白
     *
     * @param s 字符串
     */
    public static boolean isBlank(String s) {
        if (isEmpty(s)) {
            return true;
        } else {
            for (int i = 0, l = s.length(); i < l; ++i) {
                if (!isWhitespace(s.codePointAt(i))) {
                    return false;
                }
            }

            return true;
        }
    }

    /**
     * 检查字符串是否不为空白
     *
     * @param s 字符串
     */
    public static boolean isNotBlank(String s) {
        return !isBlank(s);
    }

    /**
     * 检查是否为空白字符
     *
     * @param c 字符
     */
    public static boolean isWhitespace(int c) {
        return c == 32 || c == 9 || c == 10 || c == 12 || c == 13;
    }

    /// //////////////////////////////////////////////

    /**
     * 不能为空集合
     */
    public static void notEmpty(@Nullable Collection<?> collection, String message) {
        if (isEmpty(collection)) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 不能为空集合
     */
    public static void notEmpty(@Nullable Map<?, ?> map, String message) {
        if (isEmpty(map)) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 不能为空字符串
     */
    public static void notEmpty(@Nullable String text, String message) {
        if (isEmpty(text)) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 不能为空白字符串
     */
    public static void notBlank(@Nullable String text, String message) {
        if (isBlank(text)) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 不能为 null
     */
    public static void notNull(@Nullable Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }
}
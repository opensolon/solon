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
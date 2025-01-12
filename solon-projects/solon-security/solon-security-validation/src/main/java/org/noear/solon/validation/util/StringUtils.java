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
package org.noear.solon.validation.util;

/**
 *
 * @author noear
 * @since 1.0
 * */
public class StringUtils {
    /**
     * 是否全是数字
     */
    public static boolean isDigits(String str) {
        if (str != null && str.length() != 0) {
            int l = str.length();

            for (int i = 0; i < l; ++i) {
                if (!Character.isDigit(str.codePointAt(i))) {
                    return false;
                }
            }

            return true;
        } else {
            return false;
        }
    }

    /**
     * 是否会整型
     * */
    public static boolean isInteger(String str){
        return isNumberDo(str, false);
    }

    /**
     * 是否为数字
     * */
    public static boolean isNumber(String str) {
        return isNumberDo(str, true);
    }

    /**
     * 是否为数值（可以是整数 或 小数 或 负数）
     */
    private static boolean isNumberDo(String str, boolean incDot) {
        if (str != null && str.length() != 0) {
            char[] chars = str.toCharArray();
            int l = chars.length;

            int start = chars[0] != '-' && chars[0] != '+' ? 0 : 1;
            boolean hasDot = false;

            for (int i = start; i < l; ++i) {
                int ch = chars[i];

                if(incDot) {
                    if (ch == 46) {
                        if (hasDot) {
                            return false;
                        } else {
                            hasDot = true;
                            continue;
                        }
                    }
                }

                if (!Character.isDigit(ch)) {
                    return false;
                }
            }

            return true;
        } else {
            return false;
        }
    }
}

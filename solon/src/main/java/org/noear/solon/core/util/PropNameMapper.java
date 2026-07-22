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

import org.noear.solon.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 属性名宽松映射（轻量版 Relaxed Binding）
 *
 * @author noear
 * @since 4.0
 */
public final class PropNameMapper {
    private PropNameMapper() {
    }

    /**
     * 查找候选名（有序，精确键优先）。
     */
    public static List<String> candidates(String key) {
        if (key == null || key.isEmpty()) {
            return Collections.emptyList();
        }

        String alt = alternate(key);
        if (alt == null) {
            return Collections.singletonList(key);
        }

        List<String> list = new ArrayList<>(2);
        list.add(key);
        list.add(alt);
        return list;
    }

    /**
     * 唯一别名（不含精确键本身）。无别名时返回 {@code null}（避免 List 分配）。
     * <p>
     * 热路径不预检 {@link #mayHaveAlternate}：{@link #camelToKebab} 对无驼峰键
     * 已是单遍零分配，预检反而会让有驼峰键扫描两遍。
     *
     * @since 4.0
     */
    public static String alternate(String key) {
        if (key == null || key.isEmpty()) {
            return null;
        }

        if (key.indexOf('-') >= 0) {
            String camel = Utils.snakeToCamel(key);
            return key.equals(camel) ? null : camel;
        }

        String kebab = camelToKebab(key);
        return key.equals(kebab) ? null : kebab;
    }

    /**
     * 快速判断：键是否可能存在 kebab/camel 别名。
     * <p>
     * 含 {@code -} 或存在「小写+ 紧跟大写」驼峰边界时为 true；
     * 全小写无连字符等简单键为 false。
     * 供调试/外部预筛使用；热路径 {@link #alternate} 不依赖本方法（避免双扫描）。
     *
     * @since 4.0
     */
    public static boolean mayHaveAlternate(String key) {
        if (key == null || key.isEmpty()) {
            return false;
        }
        if (key.indexOf('-') >= 0) {
            return true;
        }
        for (int i = 1; i < key.length(); i++) {
            if (isUpperAscii(key.charAt(i)) && isLowerAscii(key.charAt(i - 1))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 别名候选（不含精确键本身）。
     * <p>
     * 热路径请优先使用 {@link #alternate(String)}，可少一次集合分配。
     */
    public static List<String> alternates(String key) {
        String alt = alternate(key);
        if (alt == null) {
            return Collections.emptyList();
        }
        return Collections.singletonList(alt);
    }

    /**
     * 逻辑键：用于子视图去重。含 {@code -} 时转为 camel，否则原样。
     * <p>
     * 默认以 camel 作为逻辑键，便于 Java Bean 字段绑定。
     */
    public static String logicalKey(String key) {
        if (key == null || key.isEmpty()) {
            return key;
        }
        if (key.indexOf('-') >= 0) {
            return Utils.snakeToCamel(key);
        }
        return key;
    }

    /**
     * 驼峰转 kebab-case。
     * <p>
     * 仅在存在「小写字母后紧跟大写字母」时转换；全大写或无驼峰特征时原样返回（零分配）。
     * 配置键几乎均为 ASCII，大写判断走快路径。
     */
    public static String camelToKebab(String name) {
        if (name == null || name.isEmpty() || name.indexOf('-') >= 0) {
            return name;
        }

        // 单遍：无驼峰特征时不分配；发现首个 lower+UPPER 边界后再构建
        StringBuilder sb = null;
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);

            if (sb == null) {
                if (i > 0 && isUpperAscii(c) && isLowerAscii(name.charAt(i - 1))) {
                    sb = new StringBuilder(name.length() + 4);
                    appendCamelPrefixAsKebab(sb, name, i);
                    sb.append('-').append(toLowerAscii(c));
                }
                // 尚未确认驼峰：继续扫描，零分配
            } else {
                if (isUpperAscii(c)) {
                    if (i > 0) {
                        sb.append('-');
                    }
                    sb.append(toLowerAscii(c));
                } else {
                    sb.append(c);
                }
            }
        }

        return sb == null ? name : sb.toString();
    }

    /**
     * 将 [0, end) 按「大写前插 - 并转小写」写入（与确认驼峰后的全串规则一致）。
     */
    private static void appendCamelPrefixAsKebab(StringBuilder sb, String name, int end) {
        for (int j = 0; j < end; j++) {
            char cj = name.charAt(j);
            if (isUpperAscii(cj)) {
                if (j > 0) {
                    sb.append('-');
                }
                sb.append(toLowerAscii(cj));
            } else {
                sb.append(cj);
            }
        }
    }

    private static boolean isUpperAscii(char c) {
        return c >= 'A' && c <= 'Z';
    }

    private static boolean isLowerAscii(char c) {
        return c >= 'a' && c <= 'z';
    }

    private static char toLowerAscii(char c) {
        // 仅在 isUpperAscii 为 true 时调用
        return (char) (c + 32);
    }
}

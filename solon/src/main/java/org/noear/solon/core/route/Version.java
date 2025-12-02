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
package org.noear.solon.core.route;


/**
 * 接口版本号对象（高性能实现）
 * 
 * @author noear
 * @since 3.7
 */
public class Version implements Comparable<Version> {
    //主版本号
    private final int major;
    //次版本号
    private final int minor;
    //修订版本号
    private final int patch;
    //是否为模式匹配（如 1.0+）
    private final boolean isPattern;
    //原始版本字符串
    private final String original;

    public Version(String version) {
        this.original = version;

        String parsedVersion = version;
        if (version.endsWith("+")) {
            this.isPattern = true;
            parsedVersion = version.substring(0, version.length() - 1);
        } else {
            this.isPattern = false;
        }

        // 快速解析版本号（避免正则表达式）
        int[] parts = parseVersionParts(parsedVersion);
        this.major = parts[0];
        this.minor = parts[1];
        this.patch = parts[2];
    }

    /**
     * 快速解析版本号部分（避免split和正则）
     */
    private static int[] parseVersionParts(String version) {
        int[] parts = new int[]{0, 0, 0}; // major, minor, patch
        if (version.isEmpty()) {
            return parts;
        }

        int partIndex = 0;
        int start = 0;
        int length = version.length();

        for (int i = 0; i < length && partIndex < 3; i++) {
            char c = version.charAt(i);
            if (c == '.') {
                if (i > start) {
                    parts[partIndex] = fastParseInt(version, start, i);
                }
                partIndex++;
                start = i + 1;
            }
        }

        // 处理最后一部分
        if (start < length && partIndex < 3) {
            parts[partIndex] = fastParseInt(version, start, length);
        }

        return parts;
    }

    /**
     * 快速解析整数（避免Integer.parseInt的开销）
     */
    private static int fastParseInt(String s, int start, int end) {
        int result = 0;
        for (int i = start; i < end; i++) {
            char c = s.charAt(i);
            if (c >= '0' && c <= '9') {
                result = result * 10 + (c - '0');
            } else {
                // 遇到非数字字符，停止解析
                break;
            }
        }
        return result;
    }


    /**
     * 检查此版本（路由版本）是否满足请求版本的要求。
     * 如果路由版本是 1.0.0，请求版本是 1.2.0，则匹配（1.2.0 >= 1.0.0）。
     */
    public boolean includes(Version requestVersion) {
        if (requestVersion == null) {
            // 如果请求没有版本号，通常认为是匹配（或根据业务逻辑决定）
            return true;
        }

        if (!this.isPattern) {
            // 如果不是模式，只匹配完全相同或更高的版本（等同于 matches 方法）
            return this.compareTo(requestVersion) == 0;
        }

        // 如果是 X.Y+ 模式，则匹配所有大于或等于 X.Y 的版本
        // 此时 patch 版本没有意义
        int majorDiff = Integer.compare(this.major, requestVersion.major);
        if (majorDiff != 0) {
            return majorDiff <= 0;
        }

        int minorDiff = Integer.compare(this.minor, requestVersion.minor);
        if (minorDiff != 0) {
            return minorDiff <= 0;
        }

        // 如果主次版本号都相同，则任何修订版本号都匹配
        return true;
    }

    @Override
    public int compareTo(Version other) {
        if (other == null) {
            return -1;
        }

        // 按主版本、次版本、修订版本顺序比较
        int majorDiff = Integer.compare(other.major, this.major);
        if (majorDiff != 0) {
            return majorDiff;
        }

        int minorDiff = Integer.compare(other.minor, this.minor);
        if (minorDiff != 0) {
            return minorDiff; //<0 表示请求的更新（1.0 < 1.1）
        }

        int patchDiff = Integer.compare(other.patch, this.patch);
        if (patchDiff == 0) {
            if (other.isPattern) {
                return -1;
            }
        }

        return patchDiff;
    }


    @Override
    public String toString() {
        return original;
    }

    // Getters
    public String getOriginal() { return original; }
}
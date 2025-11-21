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

import java.util.concurrent.ConcurrentHashMap;

/**
 * 版本号对象（高性能实现）
 * 
 * @author noear
 * @since 3.4
 */
public class Version implements Comparable<Version> {
    /**
     * 版本号缓存（避免重复解析）
     */
    private static final ConcurrentHashMap<String, Version> CACHE = new ConcurrentHashMap<>();
    
    /**
     * 主版本号
     */
    private final int major;
    
    /**
     * 次版本号
     */
    private final int minor;
    
    /**
     * 修订版本号
     */
    private final int patch;
    
    /**
     * 是否为模式匹配（如 1.0+）
     */
    private final boolean isPattern;
    
    /**
     * 原始版本字符串
     */
    private final String original;
    
    /**
     * 缓存的哈希值
     */
    private final int hashCode;
    
    private Version(String version) {
        this.original = version;
        
        if (version.endsWith("+")) {
            this.isPattern = true;
            version = version.substring(0, version.length() - 1);
        } else {
            this.isPattern = false;
        }
        
        // 快速解析版本号（避免正则表达式）
        int[] parts = parseVersionParts(version);
        this.major = parts[0];
        this.minor = parts[1];
        this.patch = parts[2];
        this.hashCode = computeHashCode();
    }
    
    /**
     * 获取版本对象（带缓存）
     */
    public static Version of(String version) {
        if (version == null || version.isEmpty()) {
            return null;
        }
        return CACHE.computeIfAbsent(version, Version::new);
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
     * 计算哈希值
     */
    private int computeHashCode() {
        int result = major;
        result = 31 * result + minor;
        result = 31 * result + patch;
        result = 31 * result + (isPattern ? 1 : 0);
        return result;
    }
    
    /**
     * 检查是否匹配请求版本
     */
    public boolean matches(String requestVersion) {
        if (requestVersion == null || requestVersion.isEmpty()) {
            return false;
        }
        
        if (!isPattern) {
            // 精确匹配：直接字符串比较（最快）
            return original.equals(requestVersion);
        }
        
        // 模式匹配：比较版本号大小
        Version reqVersion = of(requestVersion);
        if (reqVersion == null) {
            return false;
        }
        
        return this.compareTo(reqVersion) <= 0; // baseVersion <= requestVersion
    }
    
    @Override
    public int compareTo(Version other) {
        if (other == null) {
            return 1;
        }
        
        // 按主版本、次版本、修订版本顺序比较
        int majorDiff = Integer.compare(this.major, other.major);
        if (majorDiff != 0) {
            return majorDiff;
        }
        
        int minorDiff = Integer.compare(this.minor, other.minor);
        if (minorDiff != 0) {
            return minorDiff;
        }
        
        return Integer.compare(this.patch, other.patch);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Version version = (Version) obj;
        return major == version.major &&
               minor == version.minor &&
               patch == version.patch &&
               isPattern == version.isPattern;
    }
    
    @Override
    public int hashCode() {
        return hashCode;
    }
    
    @Override
    public String toString() {
        return original;
    }
    
    // Getters
    public int getMajor() { return major; }
    public int getMinor() { return minor; }
    public int getPatch() { return patch; }
    public boolean isPattern() { return isPattern; }
    public String getOriginal() { return original; }
    
    /**
     * 清理缓存（用于测试或内存管理）
     */
    public static void clearCache() {
        CACHE.clear();
    }
    
    /**
     * 获取缓存大小（用于监控）
     */
    public static int getCacheSize() {
        return CACHE.size();
    }
}
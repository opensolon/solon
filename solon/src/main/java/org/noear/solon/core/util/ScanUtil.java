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

import org.noear.solon.core.AppClassLoader;

import java.util.*;
import java.util.function.Predicate;

/**
 * 资源扫描工具（用于扫描插件配置等资源...）
 *
 * @author noear
 * @author 馒头虫/瓢虫
 * @since 1.0
 * */
public class ScanUtil {
    static Scanner global;

    static {
        //（静态扩展约定：org.noear.solon.extend.impl.XxxxExt）
        global = ClassUtil.tryInstance("org.noear.solon.extend.impl.ScannerExt");

        if (global == null) {
            global = new Scanner();
        }
    }

    /**
     * 设置扫描器（用户层扩展）
     */
    public static void setScanner(Scanner scanner) {
        if (scanner != null) {
            ScanUtil.global = scanner;
        }
    }

    /**
     * 扫描路径下的的资源（path 扫描路径）
     *
     * @param path   路径
     * @param filter 过滤条件
     */
    public static Set<String> scan(String path, Predicate<String> filter) {
        return scan(AppClassLoader.global(), path, filter);
    }

    /**
     * 扫描路径下的的资源（path 扫描路径）
     *
     * @param classLoader 类加载器
     * @param path        路径
     * @param filter      过滤条件
     */
    public static Set<String> scan(ClassLoader classLoader, String path, Predicate<String> filter) {
        return global.scan(classLoader, path, false, filter);
    }

    /**
     * 扫描路径下的的资源（path 扫描路径）
     *
     * @param classLoader 类加载器
     * @param path        路径
     * @param fileMode    文件模式
     * @param filter      过滤条件
     */
    public static Set<String> scan(ClassLoader classLoader, String path, boolean fileMode, Predicate<String> filter) {
        return global.scan(classLoader, path, fileMode, filter);
    }
}
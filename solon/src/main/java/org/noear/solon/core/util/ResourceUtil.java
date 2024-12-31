/*
 * Copyright 2017-2024 noear.org and authors
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.AppClassLoader;

/**
 * 资源工具
 *
 * @author noear
 * @since 2.0
 */
public class ResourceUtil {
    public static final String TAG_file = "file:";
    public static final String TAG_classpath = "classpath:";
    public static final String TAG_classpath_ = "classpath*:"; //为了兼容用户旧的习惯

    /**
     * 是否有 "file:" 开头标识
     */
    public static boolean hasFile(String path) {
        return path.startsWith(TAG_file);
    }


    /**
     * 是否有 "classpath:" 或 "classpath*:" 开头标识
     */
    public static boolean hasClasspath(String path) {
        return path.startsWith(TAG_classpath) || path.startsWith(TAG_classpath_);
    }

    /**
     * 移除架构（开头标识）
     */
    public static String remSchema(String path) {
        //在移除之前，面要先做判断是否有
        int idx = path.indexOf(':');

        if (idx == 4 || idx == 9 || idx == 10) {
            return path.substring(idx + 1);
        } else {
            return path;
        }
    }

    /**
     * 获取资源URL集
     *
     * @param name 内部资源名称
     */
    public static Enumeration<URL> getResources(String name) throws IOException {
        return getResources(null, name);
    }

    /**
     * 获取资源URL集
     *
     * @param classLoader 类加载器
     * @param name        内部资源名称
     */
    public static Enumeration<URL> getResources(ClassLoader classLoader, String name) throws IOException {
        if (classLoader == null) {
            return AppClassLoader.global().getResources(name);
        } else {
            return classLoader.getResources(name);
        }
    }

    /**
     * 是否有资源
     *
     * @param name 内部资源名称
     */
    public static boolean hasResource(String name) {
        return getResource(name) != null;
    }

    /**
     * 是否有资源
     *
     * @param name 内部资源名称
     */
    public static boolean hasResource(ClassLoader classLoader, String name) {
        return getResource(classLoader, name) != null;
    }


    /**
     * 获取资源URL
     *
     * @param name 内部资源名称
     */
    public static URL getResource(String name) {
        return getResource(null, name); //Utils.class.getResource(name);
    }

    /**
     * 获取资源URL
     *
     * @param classLoader 类加载器
     * @param name        内部资源名称
     */
    public static URL getResource(ClassLoader classLoader, String name) {
        if (classLoader == null) {
            return AppClassLoader.global().getResource(name);
        } else {
            return classLoader.getResource(name); //Utils.class.getResource(name);
        }
    }

    /**
     * 获取资源并转为String
     *
     * @param name 内部资源名称
     */
    public static String getResourceAsString(String name) throws IOException {
        return getResourceAsString(AppClassLoader.global(), name, Solon.encoding());
    }

    /**
     * 获取资源并转为String
     *
     * @param name    内部资源名称
     * @param charset 编码
     */
    public static String getResourceAsString(String name, String charset) throws IOException {
        return getResourceAsString(AppClassLoader.global(), name, charset);
    }

    /**
     * 获取资源并转为String
     *
     * @param classLoader 类加载器
     * @param name        内部资源名称
     * @param charset     编码
     */
    public static String getResourceAsString(ClassLoader classLoader, String name, String charset) throws IOException {
        URL url = getResource(classLoader, name);
        if (url != null) {
            try (InputStream in = url.openStream()) {
                return IoUtil.transferToString(in, charset);
            }
        } else {
            return null;
        }
    }

    /**
     * 获取资源并转为 InputStream
     *
     * @param name 内部资源名称
     */
    public static InputStream getResourceAsStream(String name) throws IOException {
        return getResourceAsStream(AppClassLoader.global(), name);
    }

    /**
     * 获取资源并转为 InputStream
     *
     * @param classLoader 类加载器
     * @param name        内部资源名称
     */
    public static InputStream getResourceAsStream(ClassLoader classLoader, String name) throws IOException {
        URL url = getResource(classLoader, name);
        if (url != null) {
            return url.openStream();
        } else {
            return null;
        }
    }

    /**
     * 获取文件资源地址
     *
     * @param uri 资源地址（"./demo.xxx"）
     */
    public static URL getResourceByFile(String uri) {
        try {
            File file = Utils.getFile(uri);

            if (file.exists() == false) {
                return null;
            } else {
                return file.toURI().toURL();
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }


    /**
     * 查找资源
     *
     * @param uri 资源地址（"classpath:demo.xxx" or "./demo.xxx"）
     */
    public static String findResourceAsString(String uri) throws IOException {
        URL url = findResource(uri);
        if (url != null) {
            try (InputStream in = url.openStream()) {
                return IoUtil.transferToString(in, Solon.encoding());
            }
        } else {
            return null;
        }
    }

    /**
     * 查找资源
     *
     * @param uri 资源地址（"classpath:demo.xxx" or "./demo.xxx"）
     */
    public static URL findResource(String uri) {
        return findResource(null, uri, true);
    }

    /**
     * 查找资源
     *
     * @param uri       资源地址（"classpath:demo.xxx" or "./demo.xxx"）
     * @param defAsFile 没前缀时默认做为 file
     */
    public static URL findResource(String uri, boolean defAsFile) {
        return findResource(null, uri, defAsFile);
    }

    /**
     * 查找资源
     *
     * @param uri 资源地址（"classpath:demo.xxx" or "file:./demo.xxx" or "./demo.xxx"）
     */
    public static URL findResource(ClassLoader classLoader, String uri) {
        return findResource(classLoader, uri, true);
    }

    /**
     * 查找资源
     *
     * @param uri       资源地址（"classpath:demo.xxx" or "file:./demo.xxx" or "./demo.xxx" or "demo.xxx"）
     * @param defAsFile 没前缀时默认做为 file
     */
    public static URL findResource(ClassLoader classLoader, String uri, boolean defAsFile) {
        if (hasClasspath(uri)) {
            //classpath:..
            return getResource(classLoader, remSchema(uri));
        }

        if (hasFile(uri)) {
            //file:..
            return getResourceByFile(remSchema(uri));
        }

        if (defAsFile) {
            return getResourceByFile(uri);
        } else {
            return getResource(classLoader, uri);
        }
    }

    public static URL findResourceOrFile(ClassLoader classLoader, String uri) {
        if (hasClasspath(uri)) {
            //classpath:..
            return getResource(classLoader, remSchema(uri));
        }

        if (hasFile(uri)) {
            //file:..
            return getResourceByFile(remSchema(uri));
        }

        //选找外部，再找内部
        URL tmp = getResourceByFile(uri);

        if (tmp != null) {
            return tmp;
        } else {
            return getResource(classLoader, uri);
        }
    }


    //A.class
    //a.*.class
    //a.**.*.class
    //a.**.b.*.class

    //a.*
    //a.**.*
    //a.**.b.*
    //a.**.b.*Mapper
    //a.**.b //算包名
    //a.**.B //算类名


    /**
     * 扫描类
     *
     * @param clzExpr 类表达式（基于 import 表达式扩展）
     * @deprecated 3.0
     */
    @Deprecated
    public static Collection<Class<?>> scanClasses(String clzExpr) {
        return ClassUtil.scanClasses(clzExpr);
    }

    /**
     * 扫描类
     *
     * @param classLoader 类加载器
     * @param clzExpr     类名表达式（基于 import 表达式扩展）
     * @deprecated 3.0
     */
    @Deprecated
    public static Collection<Class<?>> scanClasses(ClassLoader classLoader, String clzExpr) {
        return ClassUtil.scanClasses(classLoader, clzExpr);
    }

    //a.xml
    //a.*.xml
    //classpath:a/**/*.xml
    //classpath:a/**/b/*.xml

    /**
     * 扫描资源
     *
     * @param resExpr 资源表达式
     */
    public static Collection<String> scanResources(String resExpr) {
        return scanResources(AppClassLoader.global(), resExpr);
    }

    /**
     * 扫描资源
     *
     * @param classLoader 类加载器
     * @param resExpr     资源表达式
     */
    public static Collection<String> scanResources(ClassLoader classLoader, String resExpr) {
        List<String> paths = new ArrayList<>();

        if (hasClasspath(resExpr)) {
            resExpr = remSchema(resExpr);
        }

        if (resExpr.startsWith("/")) {
            resExpr = resExpr.substring(1);
        }

        int xinIdx = resExpr.indexOf('*');
        if (xinIdx < 0) { //说明没有*符
            paths.add(resExpr);
            return paths;
        }

        //确定没有星号的起始目录
        int dirIdx = resExpr.indexOf('/');

        if (dirIdx < 1 || dirIdx > xinIdx) {
            throw new IllegalArgumentException("Expressions without a first-level directory are not supported: " + resExpr);
        }

        while (true) {
            int tmp = resExpr.indexOf('/', dirIdx + 1);
            if (tmp > dirIdx && tmp < xinIdx) {
                dirIdx = tmp;
            } else {
                break;
            }
        }


        String dir = resExpr.substring(0, dirIdx);

        //确定后缀
        int sufIdx = resExpr.lastIndexOf('.');
        String suf = null;
        if (sufIdx > 0) {
            suf = resExpr.substring(sufIdx);
            if (suf.indexOf('*') >= 0) {
                sufIdx = -1;
                suf = null;
            }
        }

        int sufIdx2 = sufIdx;
        String suf2 = suf;
        String expr = resExpr;

        //匹配表达式
        //"/**"
        expr = expr.replace("/**", "(/[^/]#)#"); //#暂代*
        //"*"
        expr = expr.replace("*", "[^/]+");
        //"#"
        expr = expr.replace('#', '*'); //#转回*

        Pattern pattern = Pattern.compile(expr);

        ScanUtil.scan(classLoader, dir, n -> {
                    //进行后缀过滤，相对比较快
                    if (sufIdx2 > 0) {
                        return n.endsWith(suf2);
                    } else {
                        return true;
                    }
                })
                .forEach(uri -> {
                    //再进行表达式过滤
                    if (pattern.matcher(uri).find()) {
                        paths.add(uri);
                    }
                });

        return paths;
    }
}
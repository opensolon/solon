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
    public static final String TAG_classpath = "classpath:";
    public static final String TAG_classpath_ = "classpath*:"; //为了兼容用户旧的习惯

    public static boolean hasClasspath(String path) {
        return path.startsWith(TAG_classpath) || path.startsWith(TAG_classpath_);
    }

    public static String remClasspath(String path) {
        int idx = path.indexOf(":");

        if (idx == 9 || idx == 10) {
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
     * @param name        内部资源名称
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
        return findResource(null, uri);
    }

    /**
     * 查找资源
     *
     * @param uri 资源地址（"classpath:demo.xxx" or "./demo.xxx"）
     */
    public static URL findResource(ClassLoader classLoader, String uri) {
        if (hasClasspath(uri)) {
            return getResource(classLoader, remClasspath(uri));
        } else {
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
     */
    public static Collection<Class<?>> scanClasses(String clzExpr) {
        return scanClasses(AppClassLoader.global(), clzExpr);
    }

    /**
     * 扫描类
     *
     * @param classLoader 类加载器
     * @param clzExpr     类表达式（基于 import 表达式扩展）
     */
    public static Collection<Class<?>> scanClasses(ClassLoader classLoader, String clzExpr) {
        List<Class<?>> clzList = new ArrayList<>();


        if (clzExpr.indexOf("*") < 0) {
            //说明是单个类
            String className;

            if (clzExpr.endsWith(".class")) {
                className = clzExpr.substring(0, clzExpr.length() - 6);
            } else {
                className = clzExpr;
            }

            Class<?> clz = ClassUtil.loadClass(classLoader, className);
            if (clz != null) {
                clzList.add(clz);
            }

            return clzList;
        }else {
            //说明是一批类
            if (clzExpr.endsWith(".class")) {
                clzExpr = clzExpr.substring(0, clzExpr.length() - 6);
            } else {
                int idx = clzExpr.lastIndexOf('.');
                if (clzExpr.indexOf('*', idx) < 0) {
                    //.后面没带*，可能是包名
                    if (Character.isLetter(clzExpr.charAt(idx + 1))) { //44=$ 97=a
                        //开头为小写，算作是包名
                        clzExpr = clzExpr + ".*";
                    }
                }
            }
        }


        clzExpr = clzExpr.replace(".", "/");
        clzExpr = clzExpr + ".class"; //查找时要带 class

        ResourceUtil.scanResources(classLoader, clzExpr).forEach(name -> {
            String className = name.substring(0, name.length() - 6);
            className = className.replace("/", ".");

            Class<?> clz = ClassUtil.loadClass(classLoader, className);
            if (clz != null) {
                clzList.add(clz);
            }
        });

        return clzList;
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
            resExpr = remClasspath(resExpr);
        }

        if(resExpr.startsWith("/")){
            resExpr = resExpr.substring(1);
        }

        if (resExpr.contains("/*") == false) { //说明没有*符
            paths.add(resExpr);
            return paths;
        }

        //确定没有星号的起始目录
        int dirIdx = resExpr.indexOf("/*");

        if (dirIdx < 1) {
            throw new IllegalArgumentException("Expressions without a first-level directory are not supported: " + resExpr);
        }

        String dir = resExpr.substring(0, dirIdx);

        //确定后缀
        int sufIdx = resExpr.lastIndexOf(".");
        String suf = null;
        if (sufIdx > 0) {
            suf = resExpr.substring(sufIdx);
            if (suf.contains("*")) {
                sufIdx = -1;
                suf = null;
            }
        }

        int sufIdx2 = sufIdx;
        String suf2 = suf;
        String expr = resExpr;

        //匹配表达式
        //"/**"
        expr = expr.replaceAll("/\\*\\*", "(/[^/]#)#"); //#暂代*
        //"*"
        expr = expr.replaceAll("\\*", "[^/]+");
        //"#"
        expr = expr.replaceAll("#", "*"); //#转回*

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

package org.noear.solon.core.util;

import org.noear.solon.Utils;
import org.noear.solon.core.JarClassLoader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author noear
 * @since 2.0
 */
public class ResourceUtil {

    //A.class
    //a.*.class
    //a.**.*.class
    //a.**.b.*.class

    //a.*
    //a.**.*
    //a.**.b.*

    /**
     * 扫描类
     *
     * @param clzExpr 类表达式（基于 import 表达式扩展）
     */
    public static Collection<Class<?>> scanClasses(String clzExpr) {
        return scanClasses(JarClassLoader.global(), clzExpr);
    }

    public static Collection<Class<?>> scanClasses(ClassLoader classLoader, String clzExpr) {
        List<Class<?>> clzList = new ArrayList<>();

        //说明是单个类
        if (clzExpr.indexOf("*") < 0) {
            String className;

            if (clzExpr.endsWith(".class")) {
                className = clzExpr.substring(0, clzExpr.length() - 6);
            } else {
                className = clzExpr;
            }

            Class<?> clz = Utils.loadClass(classLoader, className);
            if (clz != null) {
                clzList.add(clz);
            }

            return clzList;
        }

        if (clzExpr.endsWith(".class") == false) {
            clzExpr = clzExpr + ".class";
        }

        clzExpr = clzExpr.replace(".", "/");
        clzExpr = clzExpr.replace("/class", ".class");//xxx.class 会变成 xxx/class


        ResourceUtil.scanResources(classLoader, clzExpr).forEach(name -> {
            String className = name.substring(0, name.length() - 6);
            className = className.replace("/", ".");

            Class<?> clz = Utils.loadClass(classLoader, className);
            if (clz != null) {
                clzList.add(clz);
            }
        });

        return clzList;
    }

    //a.xml
    //a.*.xml
    //a/**/*.xml
    //a/**/b/*.xml

    /**
     * 扫描资源
     *
     * @param resExpr 资源表达式
     */
    public static Collection<String> scanResources(String resExpr) {
        return scanResources(JarClassLoader.global(), resExpr);
    }

    public static Collection<String> scanResources(ClassLoader classLoader, String resExpr) {
        List<String> paths = new ArrayList<>();

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

        //匹配表达式
        String expr = resExpr.replaceAll("/\\*\\.", "/[^\\./]+\\.");
        expr = expr.replaceAll("/\\*\\*/", "(/[^/]*)*/");
        expr = expr.replaceAll("/\\*/", "/[^/]+/");

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

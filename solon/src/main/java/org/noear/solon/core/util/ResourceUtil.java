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

    //a.class
    //a.*.class
    //a.**.*.class
    //a.**.b.*.class

    /**
     * @param packExpr 包表达式
     */
    public static Collection<Class<?>> resolveClasses(String packExpr) {
        return resolveClasses(JarClassLoader.global(), packExpr);
    }

    public static Collection<Class<?>> resolveClasses(ClassLoader classLoader, String packExpr) {
        List<Class<?>> clzList = new ArrayList<>();

        if (packExpr.endsWith(".*")) {
            packExpr = packExpr + ".class";
        }


        if (packExpr.indexOf("*") < 0 && packExpr.endsWith(".class")) {
            String className = packExpr.substring(0, packExpr.length() - 6);
            Class<?> clz = Utils.loadClass(classLoader, className);
            if (clz != null) {
                clzList.add(clz);
            }
            return clzList;
        }

        packExpr = packExpr.replace(".", "/");
        packExpr = packExpr.replace("/class", ".class");//xxx.class 会变成 xxx/class

        if (packExpr.endsWith(".class") == false) {
            packExpr = packExpr + "/*.class";
        }


        ResourceUtil.resolvePaths(classLoader, packExpr).forEach(name -> {
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
     * @param pathExpr 路径表达式
     */
    public static Collection<String> resolvePaths(String pathExpr) {
        return resolvePaths(JarClassLoader.global(), pathExpr);
    }

    public static Collection<String> resolvePaths(ClassLoader classLoader, String pathExpr) {
        List<String> paths = new ArrayList<>();

        if (pathExpr.contains("/*") == false) { //说明没有*符
            paths.add(pathExpr);
            return paths;
        }

        //确定没有星号的起始目录
        int dirIdx = pathExpr.indexOf("/*");

        if (dirIdx < 1) {
            throw new IllegalArgumentException("Expressions without a first-level directory are not supported: " + pathExpr);
        }

        String dir = pathExpr.substring(0, dirIdx);

        //确定后缀
        int sufIdx = pathExpr.lastIndexOf(".");
        String suf = null;
        if (sufIdx > 0) {
            suf = pathExpr.substring(sufIdx);
            if (suf.contains("*")) {
                sufIdx = -1;
                suf = null;
            }
        }

        int sufIdx2 = sufIdx;
        String suf2 = suf;

        //匹配表达式
        String expr = pathExpr.replaceAll("/\\*\\.", "/[^\\./]+\\.");
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

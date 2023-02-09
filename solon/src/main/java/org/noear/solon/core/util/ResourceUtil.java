package org.noear.solon.core.util;

import org.noear.solon.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author noear
 * @since 2.0
 */
public class ResourceUtil {
    public static Collection<Class<?>> resolveClasss(String packExpr) {
        List<Class<?>> clzList = new ArrayList<>();
        packExpr = packExpr.replace(".", "/") + "/*.class";
        ResourceUtil.resolvePaths(packExpr).forEach(name -> {
            String className = name.substring(0, name.length() - 6);
            className = className.replace("/", ".");

            Class<?> clz = Utils.loadClass(className);
            if (clz != null) {
                clzList.add(clz);
            }
        });

        return clzList;
    }

    /**
     * 例：
     * a/?.xml
     * a/??/?.xml
     * a/??/b/?.xml
     *
     * @param pathExpr 路径表达式
     * */
    public static Collection<String> resolvePaths(String pathExpr) {
        List<String> paths = new ArrayList<>();

        if (pathExpr.contains("/*") == false) { //说明没有*符
            paths.add(pathExpr);
            return paths;
        }

        //确定目录
        int dirIdx = pathExpr.indexOf("/*");
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
        String expr = pathExpr.replaceAll("/\\*\\.", "/[^\\.]*\\.");
        expr = expr.replaceAll("/\\*\\*/", "(/[^/]*)*/");

        Pattern pattern = Pattern.compile(expr);

        ScanUtil.scan(dir, n -> {
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

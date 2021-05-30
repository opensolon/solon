package org.noear.solon.core.util;

import org.noear.solon.core.handle.Action;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 路径分析器，用于路由器和路径变量分析
 *
 * @see org.noear.solon.core.route.RoutingDefault
 * @see Action
 * @author noear
 * @since 1.0
 * */
public class PathAnalyzer {
    private static Map<String,PathAnalyzer> cached = new LinkedHashMap<>();
    public static PathAnalyzer get(String expr) {
        PathAnalyzer pa = cached.get(expr);
        if (pa == null) {
            synchronized (expr.intern()) {
                pa = cached.get(expr);
                if (pa == null) {
                    pa = new PathAnalyzer(expr);
                    cached.put(expr, pa);
                }
            }
        }

        return pa;
    }


    private Pattern pattern;

    public PathAnalyzer(String expr){
        pattern = Pattern.compile(exprCompile(expr), Pattern.CASE_INSENSITIVE);
    }

    /**
     * 获取路径匹配结果
     * */
    public Matcher matcher(String uri){
        return pattern.matcher(uri);
    }

    /**
     * 检测是否匹配
     * */
    public boolean matches(String uri){
        return pattern.matcher(uri).find();
    }

    /**
     * 将路径表达式编译为正则表达式
     * */
    private static String exprCompile(String expr) {
        //替换特殊符号
        String p = expr;

        p = p.replace(".", "\\.");
        p = p.replace("$", "\\$");

        //替换中间的**值
        p = p.replace("**", ".[]");

        //替换*值
        p = p.replace("*", "[^/]*");

        //替换{x}值
        if (p.indexOf("{") >= 0) {
            if(p.indexOf("_}")>0){
                p = p.replaceAll("\\{[^\\}]+?\\_\\}", "(.+?)");
            }
            p = p.replaceAll("\\{[^\\}]+?\\}", "([^/]+?)");//不采用group name,可解决_的问题
        }

        if (p.startsWith("/") == false) {
            p = "/" + p;
        }

        p = p.replace(".[]",".*");

        //整合并输出
        return "^" + p + "$";
    }
}

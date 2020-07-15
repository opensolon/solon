package org.noear.solon.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 路径分析器
 * */
public class PathAnalyzer {
    private Pattern pattern;

    public PathAnalyzer(String path){
        pattern = Pattern.compile(expCompile(path), Pattern.CASE_INSENSITIVE);
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
    private static String expCompile(String path) {
        //替换特殊符号
        String p = path;

        p = p.replace(".", "\\.");
        p = p.replace("$", "\\$");

        //替换中间的**值
        p = p.replace("**", ".+?");

        //替换*值
        p = p.replace("*", "[^/]+");

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

        //整合并输出
        return "^" + p + "$";
    }
}

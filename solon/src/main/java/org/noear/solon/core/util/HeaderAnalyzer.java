package org.noear.solon.core.util;

import org.noear.solon.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wjc28
 * @version 1.0
 * @description: 对请求头进行分析判判断
 * @date 2024-08-19
 */
public class HeaderAnalyzer {

    /**
     * 对请求头内容分析的缓存
     */
    private static final Map<String, HeaderAnalyzer> cached = new HashMap<>();

    /**
     * 根据正则表达式获取对应的分析器
     * @param regex 正则表达式
     * @return 对应的分析器
     */
    public static HeaderAnalyzer get(String regex) {
        HeaderAnalyzer headerAnalyzer = cached.get(regex);
        if (headerAnalyzer == null) {
            Utils.locker().lock();

            try {
                headerAnalyzer = cached.computeIfAbsent(regex, k -> new HeaderAnalyzer(regex));
            }finally {
                Utils.locker().unlock();
            }
        }
        return headerAnalyzer;
    }


    /**
     * 对应的正则表达式
     */
    private Pattern pattern;

    public HeaderAnalyzer(String regex) {
        pattern = Pattern.compile(regex);
    }


    /**
     * 匹配值
     */
    public Matcher matcher(String value){
        assert pattern != null;
        return pattern.matcher(value);
    }

    public boolean matches(String value){
        return matcher(value).find();
    }
}

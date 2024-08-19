package org.noear.solon.core.route;

import org.noear.solon.core.util.HeaderAnalyzer;


import java.util.function.Predicate;

/**
 * @author wjc28
 * @version 1.0
 * @description: 请求头规则
 * @date 2024-08-19
 */
public class HeaderRule implements Predicate<String> {
    /**
     * 分析器
     */
    private HeaderAnalyzer analyzer;

    /**
     * 对应的header的key
     */
    private String correspondingKey;


    public HeaderRule(String key, String regex){
        this.correspondingKey = key;
        analyzer = HeaderAnalyzer.get(regex);
    }

    @Override
    public boolean test(String value) {
        return analyzer.matches(value);
    }


    public String getCorrespondingKey() {
        return correspondingKey;
    }
}

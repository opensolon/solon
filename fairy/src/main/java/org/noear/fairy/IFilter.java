package org.noear.fairy;

import java.util.Map;

/**
 * 过滤器
 * */
public interface IFilter {
    /**
     * 处理
     * */
    void handle(FairyConfig cfg, String url, Map<String, String> headers, Map<String, Object> args);
}

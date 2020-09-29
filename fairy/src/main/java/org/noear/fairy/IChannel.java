package org.noear.fairy;

import java.util.Map;

/**
 * 执行通道
 * */
public interface IChannel {
    /**
     * 设用
     * */
    Result call(FairyConfig cfg, String method, String url, Map<String, String> headers, Map<String, Object> args) throws Throwable;
}

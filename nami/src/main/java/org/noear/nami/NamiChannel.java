package org.noear.nami;

import java.util.Map;

/**
 * 执行通道
 * */
public interface NamiChannel {
    /**
     * 设用
     * */
    Result call(NamiConfig cfg, String method, String url, Map<String, String> headers, Map<String, Object> args) throws Throwable;
}

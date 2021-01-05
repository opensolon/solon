package org.noear.nami;

import org.noear.nami.common.Result;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Nami 执行通道
 * */
public interface NamiChannel extends Filter{
    /**
     * 设用
     * */
    Result call(NamiConfig cfg, Method method, String action, String url, Map<String, String> headers, Map<String, Object> args, Object body) throws Throwable;
}

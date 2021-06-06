package org.noear.nami;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Nami - 请求上下文
 *
 * @author noear
 * @since 1.4
 */
public class Context {
    /**
     * 配置
     * */
    public final NamiConfig config;
    /**
     * 函数
     * */
    public final Method method;

    /**
     * 动作（GET,POST...）
     * */
    public final String action;

    /**
     * 请求头信息
     * */
    public final Map<String, String> headers = new LinkedHashMap<>();
    /**
     * 请求参数
     * */
    public final Map<String, Object> args = new LinkedHashMap<>();
    /**
     * 请求体
     * */
    public Object body;
    /**
     * 请求地址
     * */
    public String url;


    public Context(NamiConfig config, Method method, String action) {
        this.config = config;
        this.method = method;
        this.action = action;
    }
}

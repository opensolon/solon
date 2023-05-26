package org.noear.nami;

import java.lang.reflect.Method;
import java.net.URI;
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
     */
    public final Config config;
    /**
     * 目标
     * */
    public final Object target;
    /**
     * 函数
     */
    public final Method method;

    /**
     * 动作（GET,POST...）
     */
    public final String action;

    /**
     * 请求头信息
     */
    public final Map<String, String> headers = new LinkedHashMap<>();
    /**
     * 请求参数
     */
    public final Map<String, Object> args = new LinkedHashMap<>();
    /**
     * 请求地址
     */
    public final String url;
    /**
     * 请求地址
     */
    public final URI uri;

    /**
     * 请求体
     */
    public Object body;


    public Context(Config config, Object target, Method method, String action, String url) {
        this.config = config;
        this.target = target;
        this.method = method;
        this.action = action;
        this.url = url;
        this.uri = URI.create(url);
    }
}

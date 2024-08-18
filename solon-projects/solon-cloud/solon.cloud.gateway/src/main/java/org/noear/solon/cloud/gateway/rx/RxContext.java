package org.noear.solon.cloud.gateway.rx;

import io.vertx.core.http.Cookie;
import io.vertx.core.http.HttpServerRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * @author noear
 * @since 2.9
 */
public class RxContext {
    private final HttpServerRequest rawRequest;
    private final RxExchange exchange;
    private final Map<String, Object> attrMap;

    public RxContext(HttpServerRequest rawRequest) {
        this.rawRequest = rawRequest;
        this.exchange = new RxExchange(rawRequest);
        this.attrMap = new HashMap<>();
    }

    /**
     * 获取路径
     */
    public String path() {
        return rawRequest.path();
    }

    /**
     * 获取 header
     */
    public String header(String key) {
        return rawRequest.getHeader(key);
    }

    /**
     * 获取 cookie
     */
    public String cookie(String key) {
        Cookie cookie = rawRequest.getCookie(key);
        if (cookie == null) {
            return null;
        } else {
            return cookie.getValue();
        }
    }

    /**
     * 交换机
     */
    public RxExchange exchange() {
        return exchange;
    }

    public <T> T attr(String key) {
        return (T) attrMap.get(key);
    }

    public void attrSet(String key, Object value) {
        attrMap.put(key, value);
    }
}

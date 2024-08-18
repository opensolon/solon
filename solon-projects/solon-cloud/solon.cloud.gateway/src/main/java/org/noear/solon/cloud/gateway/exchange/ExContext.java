package org.noear.solon.cloud.gateway.exchange;

import io.vertx.core.http.Cookie;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import org.noear.solon.cloud.gateway.integration.TimeoutProperties;
import org.noear.solon.cloud.gateway.route.Route;
import org.noear.solon.util.KeyValues;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * 交换上下文
 *
 * @author noear
 * @since 2.9
 */
public class ExContext {
    private final Map<String, Object> attrMap;
    private final HttpServerRequest rawRequest;

    private ExNewRequest newRequest;
    private ExNewResponse newResponse;

    private URI target;
    private TimeoutProperties timeout;

    public ExContext(HttpServerRequest rawRequest) {
        this.rawRequest = rawRequest;
        this.attrMap = new HashMap<>();
    }


    /**
     * 属性获取
     */
    public <T> T attr(String key) {
        return (T) attrMap.get(key);
    }

    /**
     * 属性设置
     */
    public void attrSet(String key, Object value) {
        attrMap.put(key, value);
    }

    /**
     * 获取原始路径
     */
    public String rawPath() {
        return rawRequest.path();
    }

    /**
     * 获取原始 header
     */
    public String rawHeader(String key) {
        return rawRequest.getHeader(key);
    }

    /**
     * 获取原始 cookie
     */
    public String rawCookie(String key) {
        Cookie cookie = rawRequest.getCookie(key);
        if (cookie == null) {
            return null;
        } else {
            return cookie.getValue();
        }
    }

    public void bind(Route route) {
        this.target = route.getTarget();
        this.timeout = route.getTimeout();
    }

    public URI target() {
        return target;
    }

    public TimeoutProperties timeout() {
        return timeout;
    }

    public ExNewRequest newRequest() {
        if (newRequest == null) {
            newRequest = new ExNewRequest();

            newRequest.method(rawRequest.method().name());
            newRequest.queryString(rawRequest.query());
            newRequest.path(rawRequest.path());

            for (Map.Entry<String, String> kv : rawRequest.headers().entries()) {
                newRequest.headerAdd(kv.getKey(), kv.getValue());
            }

            newRequest.body(rawRequest.body());
        }

        return newRequest;
    }

    public ExNewResponse newResponse() {
        if (newResponse == null) {
            newResponse = new ExNewResponse();
        }

        return newResponse;
    }

    public void complete() {
        HttpServerResponse rawResponse = rawRequest.response();

        rawResponse.setStatusCode(newResponse().getStatus());

        for (KeyValues<String> kv : newResponse().getHeaders().values()) {
            rawResponse.putHeader(kv.getKey(), kv.getValues());
        }

        if (newResponse().getBody() != null) {
            rawResponse.end(newResponse().getBody());
        } else {
            rawResponse.end();
        }
    }
}
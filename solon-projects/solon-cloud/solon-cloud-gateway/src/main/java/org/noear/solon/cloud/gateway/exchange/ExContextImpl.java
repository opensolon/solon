/*
 * Copyright 2017-2024 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.cloud.gateway.exchange;

import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.Cookie;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.net.SocketAddress;
import org.noear.solon.Utils;
import org.noear.solon.cloud.gateway.properties.TimeoutProperties;
import org.noear.solon.cloud.gateway.route.Route;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 交换上下文实现
 *
 * @author noear
 * @since 2.9
 */
public class ExContextImpl implements ExContext {
    private final Map<String, Object> attrMap;
    private final HttpServerRequest rawRequest;

    private ExNewRequest newRequest;
    private ExNewResponse newResponse;

    private Route route;
    private URI targetNew;

    public ExContextImpl(HttpServerRequest rawRequest) {
        this.rawRequest = rawRequest;
        this.attrMap = new HashMap<>();
    }

    public HttpServerRequest rawRequest(){
        return rawRequest;
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

    ////////////////////////////////////////////////////

    /**
     * 绑定路由信息
     */
    public void bind(Route route) {
        if (route != null) {
            this.route = route;
        }
    }

    /**
     * 路由
     */
    public Route route() {
        return route;
    }

    /**
     * 路由目标
     */
    @Override
    public URI target() {
        if (route == null) {
            return null;
        } else {
            return route.getTarget();
        }
    }

    /**
     * 配置路由新目标
     */
    @Override
    public void targetNew(URI target) {
        this.targetNew = target;
    }

    /**
     * 路由新目标
     */
    @Override
    public URI targetNew() {
        if (targetNew == null) {
            return target();
        } else {
            return targetNew;
        }
    }

    /**
     * 路由超时
     */
    @Override
    public TimeoutProperties timeout() {
        if (route == null) {
            return null;
        } else {
            return route.getTimeout();
        }
    }

    ////////////////////////////////////////////////////

    /**
     * 远程地址
     */
    public SocketAddress remoteAddress() {
        return rawRequest.remoteAddress();
    }

    /**
     * 本地地址
     */
    public SocketAddress localAddress() {
        return rawRequest.localAddress();
    }

    private String realIp;

    /**
     * 客户端真实IP
     */
    public String realIp() {
        if (realIp == null) {
            //客户端ip
            realIp = rawHeader(ExConstants.X_Real_IP);

            if (Utils.isEmpty(realIp) || "unknown".equalsIgnoreCase(realIp)) {
                //包含了客户端和各级代理ip的完整ip链路
                realIp = rawHeader(ExConstants.X_Forwarded_For);
                if (realIp != null && realIp.contains(",")) {
                    realIp = realIp.split(",")[0];
                }
            }

            if (Utils.isEmpty(realIp) || "unknown".equalsIgnoreCase(realIp)) {
                realIp = remoteAddress().host();
            }
        }

        return realIp;
    }


    /**
     * 是否安全
     */
    public boolean isSecure() {
        return rawRequest.isSSL();
    }

    ////////////////////////////////////////////////////

    /**
     * 获取原始请求方法
     */
    public String rawMethod() {
        return rawRequest.method().name();
    }

    private URI rawURI;

    /**
     * 获取原始完整请求地址 uri
     */
    public URI rawURI() {
        if (rawURI == null) {
            rawURI = URI.create(rawRequest.absoluteURI());
        }

        return rawURI;
    }


    /**
     * 获取原始路径
     */
    public String rawPath() {
        return rawRequest.path();
    }

    /**
     * 获取原始查询字符串
     */
    public String rawQueryString() {
        return rawRequest.query();
    }

    /**
     * 获取原始查询参数
     */
    public String rawQueryParam(String key) {
        return rawRequest.getParam(key);
    }

    /**
     * 获取原始所有查询参数
     */
    public MultiMap rawQueryParams() {
        return rawRequest.params();
    }

    /**
     * 获取原始 header
     */
    public String rawHeader(String key) {
        return rawRequest.getHeader(key);
    }

    /**
     * 获取原始所有 header
     */
    public MultiMap rawHeaders() {
        return rawRequest.headers();
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

    /**
     * 获取原始所有 cookie
     */
    public Set<Cookie> rawCookies() {
        return rawRequest.cookies();
    }

    /**
     * 获取原始主体
     */
    @Override
    public Future<Buffer> rawBody() {
        return rawRequest.body();
    }


    ////////////////////////////////////////////////////

    /**
     * 新的请求构建器
     */
    public ExNewRequest newRequest() {
        if (newRequest == null) {
            newRequest = new ExNewRequest();

            newRequest.method(rawRequest.method().name());
            newRequest.queryString(rawRequest.query());
            newRequest.path(rawRequest.path());

            for (Map.Entry<String, String> kv : rawRequest.headers().entries()) {
                newRequest.headerAdd(kv.getKey(), kv.getValue());
            }

            newRequest.body(rawRequest);
        }

        return newRequest;
    }

    /**
     * 新的响应构建器
     */
    public ExNewResponse newResponse() {
        if (newResponse == null) {
            newResponse = new ExNewResponse();
        }

        return newResponse;
    }
}
/*
 * Copyright 2017-2025 noear.org and authors
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
import io.vertx.core.net.SocketAddress;
import org.noear.solon.cloud.gateway.properties.TimeoutProperties;
import org.noear.solon.core.handle.Context;

import java.net.URI;
import java.util.Set;

/**
 * 交换上下文
 *
 * @author noear
 * @since 2.9
 */
public interface ExContext {
    /**
     * 属性获取
     */
    <T> T attr(String key);

    /**
     * 属性设置
     */
    void attrSet(String key, Object value);

    /**
     * 路由目标
     */
    URI target();

    /**
     * 配置路由新目标
     */
    void targetNew(URI target);

    /**
     * 路由新目标
     */
    URI targetNew();

    /**
     * 路由超时
     */
    TimeoutProperties timeout();

    ////////////////////////////////////////////////////

    /**
     * 远程地址
     */
    SocketAddress remoteAddress();

    /**
     * 本地地址
     */
    SocketAddress localAddress();

    /**
     * 客户端真实IP
     */
    String realIp();

    /**
     * 是否安全
     */
    boolean isSecure();

    ////////////////////////////////////////////////////

    /**
     * 获取原始请求方法
     */
    String rawMethod();

    /**
     * 获取原始完整请求地址 uri
     */
    URI rawURI();

    /**
     * 获取原始路径
     */
    String rawPath();

    /**
     * 获取原始查询字符串
     */
    String rawQueryString();

    /**
     * 获取原始查询参数
     */
    String rawQueryParam(String key);

    /**
     * 获取原始所有查询参数
     */
    MultiMap rawQueryParams();

    /**
     * 获取原始 header
     */
    String rawHeader(String key);

    /**
     * 获取原始所有 header
     */
    MultiMap rawHeaders();

    /**
     * 获取原始 cookie
     */
    String rawCookie(String key);

    /**
     * 获取原始所有 cookie
     */
    Set<Cookie> rawCookies();

    /**
     * 获取原始主体
     */
    Future<Buffer> rawBody();


    ////////////////////////////////////////////////////

    /**
     * 新的请求构建器
     */
    ExNewRequest newRequest();

    /**
     * 新的响应构建器
     */
    ExNewResponse newResponse();

    /**
     * 转为经典接口（不带 req-body）
     */
    Context toContext();
}
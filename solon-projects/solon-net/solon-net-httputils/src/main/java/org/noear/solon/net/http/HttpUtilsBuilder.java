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
package org.noear.solon.net.http;

import org.noear.solon.Utils;
import org.noear.solon.core.util.MultiMap;

/**
 * Http 工具构建器（提供预处理支持）
 *
 * @author noear
 * @since 3.1
 */
public class HttpUtilsBuilder {
    private String baseUri;
    private String group;
    private String service;
    private MultiMap<String> headers = new MultiMap<>();
    private HttpTimeout timeout;

    /**
     * 服务名
     */
    public HttpUtilsBuilder service(String service) {
        this.service = service;
        return this;
    }

    /**
     * 服务分组与服务名
     */
    public HttpUtilsBuilder service(String group, String service) {
        this.group = group;
        this.service = service;
        return this;
    }

    /**
     * 基础地址
     */
    public HttpUtilsBuilder baseUri(String baseUri) {
        this.baseUri = baseUri;
        return this;
    }

    /**
     * 添加头
     */
    public HttpUtilsBuilder headerAdd(String key, String value) {
        headers.add(key, value);
        return this;
    }

    /**
     * 设置头
     */
    public HttpUtilsBuilder headerSet(String key, String value) {
        headers.put(key, value);
        return this;
    }

    /**
     * 设置超时
     */
    public HttpUtilsBuilder timeout(int timeoutSeconds) {
        timeout = new HttpTimeout(timeoutSeconds);
        return this;
    }

    /**
     * 设置超时
     */
    public HttpUtilsBuilder timeout(int connectTimeoutSeconds, int writeTimeoutSeconds, int readTimeoutSeconds) {
        timeout = new HttpTimeout(connectTimeoutSeconds, writeTimeoutSeconds, readTimeoutSeconds);
        return this;
    }

    /**
     * 构建 Http 工具
     */
    public HttpUtils build(String url) {
        if (Utils.isNotEmpty(service)) {
            baseUri = LoadBalanceUtils.getServer(group, service);
        }

        if (Utils.isNotEmpty(baseUri)) {
            return HttpUtils.http(baseUri + url).headers(headers).timeout(timeout);
        } else {
            return HttpUtils.http(url).headers(headers).timeout(timeout);
        }
    }
}
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
import io.vertx.core.buffer.Buffer;
import org.noear.solon.Utils;
import org.noear.solon.util.KeyValues;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 交换新请求
 *
 * @author noear
 * @since 2.9
 */
public class ExNewRequest {
    //----------------
    private String method;
    private String queryString;
    private String path;
    private Map<String, KeyValues<String>> headers = new LinkedHashMap<>();
    private Future<Buffer> body;

    /**
     * 配置方法
     */
    public ExNewRequest method(String method) {
        this.method = method;
        return this;
    }

    /**
     * 配置路径
     */
    public ExNewRequest path(String path) {
        this.path = path;
        return this;
    }

    /**
     * 配置查询字符串
     */
    public ExNewRequest queryString(String queryString) {
        this.queryString = queryString;
        return this;
    }

    private KeyValues<String> headerHolder(String key) {
        return headers.computeIfAbsent(key, k -> new KeyValues<>(key));
    }

    /**
     * 配置头（替换）
     */
    public ExNewRequest header(String key, String... values) {
        headerHolder(key).setValues(values);
        return this;
    }

    /**
     * 配置头（替换）
     */
    public ExNewRequest header(String key, List<String> values) {
        headerHolder(key).setValues(values.toArray(new String[values.size()]));
        return this;
    }

    /**
     * 添加头（添加）
     */
    public ExNewRequest headerAdd(String key, String value) {
        headerHolder(key).addValue(value);
        return this;
    }

    /**
     * 移除头
     */
    public ExNewRequest headerRemove(String... keys) {
        for (String key : keys) {
            headers.remove(key);
        }
        return this;
    }

    /**
     * 配置主体
     *
     * @param body 主体数据
     */
    public ExNewRequest body(Future<Buffer> body) {
        this.body = body;
        return this;
    }

    //----------

    /**
     * 获取方法
     */
    public String getMethod() {
        return method;
    }

    /**
     * 获取查询字符串
     */
    public String getQueryString() {
        return queryString;
    }

    /**
     * 获取路径
     */
    public String getPath() {
        return path;
    }

    /**
     * 获取路径和查询字符串
     */
    public String getPathAndQueryString() {
        if (Utils.isEmpty(getQueryString())) {
            return getPath();
        } else {
            return getPath() + "?" + getQueryString();
        }
    }

    /**
     * 获取头集合
     */
    public Map<String, KeyValues<String>> getHeaders() {
        return headers;
    }

    /**
     * 获取主体
     */
    public Future<Buffer> getBody() {
        return body;
    }
}
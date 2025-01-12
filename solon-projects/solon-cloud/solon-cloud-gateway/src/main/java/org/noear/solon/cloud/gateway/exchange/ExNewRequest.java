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

import io.vertx.core.buffer.Buffer;
import io.vertx.core.streams.ReadStream;
import org.noear.solon.Utils;
import org.noear.solon.cloud.gateway.exchange.impl.ExBodyOfBuffer;
import org.noear.solon.cloud.gateway.exchange.impl.ExBodyOfStream;
import org.noear.solon.core.util.MultiMap;

import java.util.List;

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
    private MultiMap<String> headers = new MultiMap<>();
    private ExBody body;

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

    /**
     * 配置头（替换）
     */
    public ExNewRequest header(String key, String... values) {
        headers.holder(key).setValues(values);
        return this;
    }

    /**
     * 配置头（替换）
     */
    public ExNewRequest header(String key, List<String> values) {
        headers.holder(key).setValues(values.toArray(new String[values.size()]));
        return this;
    }

    /**
     * 添加头（添加）
     */
    public ExNewRequest headerAdd(String key, String value) {
        headers.holder(key).addValue(value);
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
     * 配置主体（方便用户修改）
     *
     * @param body 主体数据
     */
    public ExNewRequest body(Buffer body) {
        this.body = new ExBodyOfBuffer(body);
        return this;
    }

    /**
     * 配置主体（实现流式转发）
     *
     * @param body 主体数据
     */
    public ExNewRequest body(ReadStream<Buffer> body) {
        this.body = new ExBodyOfStream(body);
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
    public MultiMap<String> getHeaders() {
        return headers;
    }

    /**
     * 获取主体
     */
    public ExBody getBody() {
        return body;
    }
}
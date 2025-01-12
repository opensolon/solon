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
import org.noear.solon.boot.web.Constants;
import org.noear.solon.cloud.gateway.exchange.impl.ExBodyOfBuffer;
import org.noear.solon.cloud.gateway.exchange.impl.ExBodyOfStream;
import org.noear.solon.core.util.MultiMap;

import java.util.List;

/**
 * 交换新响应
 *
 * @author noear
 * @since 2.9
 */
public class ExNewResponse {
    private int status = 200;
    private MultiMap<String> headers = new MultiMap<>();
    private ExBody body;

    public void status(int code) {
        this.status = code;
    }

    /**
     * 配置头（替换）
     */
    public ExNewResponse header(String key, String... values) {
        headers.holder(key).setValues(values);
        return this;
    }

    /**
     * 配置头（替换）
     */
    public ExNewResponse header(String key, List<String> values) {
        headers.holder(key).setValues(values);
        return this;
    }

    /**
     * 添加头（添加）
     */
    public ExNewResponse headerAdd(String key, String value) {
        headers.holder(key).addValue(value);
        return this;
    }

    /**
     * 移除头
     */
    public ExNewResponse headerRemove(String... keys) {
        for (String key : keys) {
            headers.remove(key);
        }
        return this;
    }

    /**
     * 跳转
     */
    public ExNewResponse redirect(int code, String url) {
        status(code);
        header(Constants.HEADER_LOCATION, url);
        return this;
    }

    /**
     * 配置主体（方便用户修改）
     *
     * @param body 主体数据
     */
    public ExNewResponse body(Buffer body) {
        this.body = new ExBodyOfBuffer(body);
        return this;
    }

    /**
     * 配置主体（实现流式转发）
     *
     * @param body 主体数据
     */
    public ExNewResponse body(ReadStream<Buffer> body) {
        this.body = new ExBodyOfStream(body);
        return this;
    }

    /**
     * 获取状态
     */
    public int getStatus() {
        return status;
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

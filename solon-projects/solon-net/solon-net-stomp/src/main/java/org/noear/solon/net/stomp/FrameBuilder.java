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
package org.noear.solon.net.stomp;

import org.noear.solon.Utils;
import org.noear.solon.core.util.KeyValue;
import org.noear.solon.core.util.KeyValues;
import org.noear.solon.core.util.MultiMap;

/**
 * Stomp 帧构建器
 *
 * @author noear
 * @since 3.0
 */
public class FrameBuilder {
    private String command = "";
    private final MultiMap<String> headers = new MultiMap<>();
    private String payload;
    private String source;

    /**
     * 原始数据
     */
    public FrameBuilder source(String source) {
        this.source = source;
        return this;
    }

    /**
     * 命令
     */
    public FrameBuilder command(String command) {
        if (command != null) {
            //确保不产生 null
            this.command = command;
        }

        return this;
    }

    /**
     * 有效核载
     */
    public FrameBuilder payload(String payload) {
        this.payload = payload;
        return this;
    }

    /**
     * 头添加
     */
    public FrameBuilder headerAdd(KeyValue<String>... headers) {
        for (KeyValue<String> kv : headers) {
            this.headers.holder(kv.getKey()).addValue(kv.getValue());
        }
        return this;
    }

    /**
     * 头添加
     */
    public FrameBuilder headerAdd(Iterable<KeyValues<String>> headers) {
        for (KeyValues<String> kv : headers) {
            for (String val : kv.getValues()) {
                this.headers.holder(kv.getKey()).addValue(val);
            }
        }
        return this;
    }

    /**
     * 头添加
     */
    public FrameBuilder headerAdd(String key, String val) {
        if (key != null && val != null) {
            this.headers.holder(key).addValue(val);
        }

        return this;
    }

    /**
     * 头替换
     */
    public FrameBuilder headerSet(String key, String val) {
        if (key != null && val != null) {
            this.headers.holder(key).setValues(val);
        }

        return this;
    }

    /**
     * 内容类型头
     */
    public FrameBuilder contentType(String contentType) {
        if (Utils.isNotEmpty(contentType)) {
            return headerAdd(Headers.CONTENT_TYPE, contentType);
        }

        return this;
    }

    /**
     * 目的地头
     */
    public FrameBuilder destination(String destination) {
        if (Utils.isNotEmpty(destination)) {
            return headerAdd(Headers.DESTINATION, destination);
        }

        return this;
    }

    /**
     * 构建
     */
    public Frame build() {
        return new SimpleFrame(source, command, payload, headers);
    }
}
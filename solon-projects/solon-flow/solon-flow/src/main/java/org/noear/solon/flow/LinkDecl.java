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
package org.noear.solon.flow;

import org.noear.solon.Utils;

import java.util.HashMap;
import java.util.Map;

/**
 * 连接申明
 *
 * @author noear
 * @since 3.0
 */
public class LinkDecl {
    protected final String nextId;
    protected String title;
    protected Map<String, Object> meta;
    protected String condition;
    /**
     * 优先级（越大越高）
     */
    protected int priority;

    /**
     * @param nextId 目标 id
     */
    public LinkDecl(String nextId) {
        this.nextId = nextId;
    }

    /**
     * 配置标题
     */
    public LinkDecl title(String title) {
        this.title = title;
        return this;
    }

    /**
     * 配置元信息
     */
    public LinkDecl meta(Map<String, Object> meta) {
        this.meta = meta;
        return this;
    }

    /**
     * 配置元信息
     */
    public LinkDecl metaPut(String key, Object value) {
        if (meta == null) {
            meta = new HashMap<>();
        }

        meta.put(key, value);
        return this;
    }

    /**
     * 配置条件
     */
    public LinkDecl condition(String condition) {
        this.condition = condition;
        return this;
    }

    /**
     * 配置优先级（越大越优）
     */
    public LinkDecl priority(int priority) {
        this.priority = priority;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("{");

        buf.append("nextId='").append(nextId).append('\'');

        if (Utils.isNotEmpty(title)) {
            buf.append(", title='").append(title).append('\'');
        }

        if (Utils.isNotEmpty(condition)) {
            buf.append(", condition='").append(condition).append('\'');
        }

        if (Utils.isNotEmpty(meta)) {
            buf.append(", meta=").append(meta);
        }

        buf.append("}");

        return buf.toString();
    }
}

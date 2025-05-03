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
package org.noear.solon.core.handle;

import org.noear.solon.core.util.MultiMap;

/**
 * 处理实体
 *
 * <pre>{@code
 *  ctx.render(new Entity());
 *
 *  ctx.rerturnValue(new Entity());
 *  ctx.rerturnValue(Mono.just(new Entity().status(500))); //main case
 * }</pre>
 *
 * @author noear
 * @since 3.1
 */
public class Entity {
    private int status = 0;
    private Object body;
    private MultiMap<String> headers = new MultiMap<>();

    /**
     * 状态
     */
    public int status() {
        return status;
    }

    /**
     * 主体
     */
    public Object body() {
        return body;
    }

    /**
     * 头部
     */
    public MultiMap<String> headers() {
        return headers;
    }

    /// ///////////////////

    public Entity status(int status) {
        this.status = status;
        return this;
    }

    public Entity body(Object body) {
        this.body = body;
        return this;
    }

    public Entity headerAdd(String name, String value) {
        headers.add(name, value);
        return this;
    }

    public Entity headerRemove(String name) {
        headers.remove(name);
        return this;
    }

    public Entity headerSet(String name, String value) {
        headers.put(name, value);
        return this;
    }

    public Entity contentType(String contentType) {
        headers.put("Content-Type", contentType);
        return this;
    }
}
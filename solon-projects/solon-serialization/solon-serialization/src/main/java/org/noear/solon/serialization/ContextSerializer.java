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
package org.noear.solon.serialization;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.serialize.Serializer;
import org.noear.solon.lang.Nullable;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * 通用上下文接口序列化器
 *
 * @author noear
 * @since 2.8
 */
public interface ContextSerializer<T> extends Serializer<T> {
    /**
     * 匹配
     *
     * @param ctx  上下文
     * @param mime
     */
    boolean matched(Context ctx, String mime);

    /**
     * 媒体类型
     */
    String mimeType();

    /**
     * 必须要 body
     */
    default boolean bodyRequired() {
        return false;
    }

    /**
     * 序列化到
     *
     * @param ctx  请求上下文
     * @param data 数据
     */
    void serializeToBody(Context ctx, Object data) throws IOException;

    /**
     * 反序列化从
     *
     * @param ctx    请求上下文
     * @param toType 目标类型
     * @since 3.0
     */
    Object deserializeFromBody(Context ctx, @Nullable Type toType) throws IOException;

    /**
     * 反序列化从
     *
     * @param ctx 请求上下文
     */
    default Object deserializeFromBody(Context ctx) throws IOException {
        return deserializeFromBody(ctx, null);
    }
}
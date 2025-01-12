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
package org.noear.solon.core.serialize;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * 序列化器
 *
 * @author noear
 * @since 2.8
 */
public interface Serializer<T> {
    /**
     * 名字
     */
    default String name() {
        return this.getClass().getSimpleName();
    }

    /**
     * 媒体类型
     */
    default String mimeType() {
        return null;
    }

    /**
     * 数据类型
     */
    default Class<T> dataType() {
        return null;
    }

    /**
     * 序列化
     *
     * @param fromObj 来源对象
     */
    T serialize(Object fromObj) throws IOException;

    /**
     * 反序列化
     *
     * @param toType 目标类型
     */
    Object deserialize(T data, Type toType) throws IOException;
}
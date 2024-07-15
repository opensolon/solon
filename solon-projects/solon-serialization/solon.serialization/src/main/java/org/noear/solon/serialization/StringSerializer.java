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

import java.io.IOException;

/**
 * 字符串序列化器
 *
 * @author noear
 * @since 1.5
 * @deprecated 2.8
 * @removal true
 */
@Deprecated
public interface StringSerializer extends org.noear.solon.core.serialize.Serializer<String> {
    /**
     * 名字
     */
    default String name() {
        return this.getClass().getSimpleName();
    }

    /**
     * 序列化为字符串
     */
    String serialize(Object fromObj) throws IOException;


    default Object deserialize(String data, Class<?> toClz) throws IOException {
        return null;
    }
}
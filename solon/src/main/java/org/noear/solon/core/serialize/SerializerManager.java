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

import java.util.HashMap;
import java.util.Map;

/**
 * 序列化管理器
 *
 * @author noear
 * @since 3.0
 */
public class SerializerManager {
    private Map<String, Serializer> _mapping = new HashMap<>();

    /**
     * 注册
     *
     * @param mapping 映射
     * @param serializer 序列化器
     * */
    public <T> void register(String mapping, Serializer<T> serializer) {
        if(serializer.mimeType() == null){
            throw new IllegalArgumentException("Invalid Serializer mimeType: " + serializer.getClass().getName());
        }

        if (serializer.dataType() == null) {
            throw new IllegalArgumentException("Invalid Serializer dataType: " + serializer.getClass().getName());
        }

        _mapping.put(mapping, serializer);
        _mapping.put(serializer.name(), serializer);
    }

    /**
     * 查找序列化器
     *
     * <pre>{@code
     * Serializer<String> get("@json");
     * Serializer<String> get("fastjson-serializer");
     * }</pre>
     *
     * @param name 名字
     * */
    public <T> Serializer<T> get(String name) {
        return _mapping.get(name);
    }
}

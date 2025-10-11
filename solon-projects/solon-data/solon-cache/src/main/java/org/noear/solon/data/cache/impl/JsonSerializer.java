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
package org.noear.solon.data.cache.impl;

import org.noear.snack.ONode;
import org.noear.snack.core.Options;
import org.noear.solon.core.serialize.Serializer;
import org.noear.solon.core.util.ClassUtil;

import java.lang.reflect.Type;

/**
 * Json 序列化实现
 *
 * @author noear
 * @since 2.5
 */
public class JsonSerializer implements Serializer<String> {
    public static final JsonSerializer instance = new JsonSerializer(false);
    /**
     * @since 2.8
     */
    public static final JsonSerializer typedInstance = new JsonSerializer(true);

    private final boolean typed;

    public JsonSerializer() {
        this(false);
    }

    public JsonSerializer(boolean typed) {
        this.typed = typed;
    }

    @Override
    public String name() {
        return "json-snack3";
    }

    @Override
    public String serialize(Object fromObj) {
        if (typed) {
            return ONode.serialize(fromObj);
        } else {
            return ONode.stringify(fromObj);
        }
    }

    @Override
    public Object deserialize(String dta, Type toType) {
        Options options = Options.serialize();

        //分析类加载器
        options.setClassLoader(ClassUtil.resolveClassLoader(toType));

        return ONode.load(dta, options).toObject(toType);
    }
}
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
package io.jsonwebtoken.snack.io;

import io.jsonwebtoken.io.SerializationException;
import io.jsonwebtoken.io.Serializer;
import org.noear.snack.ONode;
import org.noear.snack.core.Feature;
import org.noear.snack.core.Options;

import java.nio.charset.StandardCharsets;

/**
 * Snack3 的 Serializer 实现，支持 bean 序列化
 *
 * @author noear
 * @since 1.10
 */
public class SnackSerializer<T> implements Serializer<T> {
    Options options = Options.serialize().add(Feature.EnumUsingName);
    @Override
    public byte[] serialize(T t) throws SerializationException {
        ONode oNode = ONode.loadObj(t, options);
        oNode.remove(options.getTypePropertyName());

        String json = oNode.toJson();
        return json.getBytes(StandardCharsets.UTF_8);
    }
}

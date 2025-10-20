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

import io.jsonwebtoken.io.DeserializationException;
import io.jsonwebtoken.io.Deserializer;
import org.noear.snack4.Feature;
import org.noear.snack4.ONode;
import org.noear.snack4.Options;

/**
 * Snack3 的 Deserializer 实现
 *
 * @author noear
 * @since 1.10
 */
public class SnackDeserializer<T> implements Deserializer<T> {
    Options options = Options.of().addFeatures(Feature.Write_EnumUsingName, Feature.Write_ClassName, Feature.Read_AutoType);

    @Override
    public T deserialize(byte[] bytes) throws DeserializationException {
        String json = new String(bytes);
        ONode oNode = ONode.ofJson(json, options);
        oNode.remove(options.getTypePropertyName());

        return oNode.toBean();
    }
}
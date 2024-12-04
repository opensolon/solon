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
package org.noear.nami.coder.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import org.noear.nami.Context;
import org.noear.nami.Decoder;
import org.noear.nami.Result;
import org.noear.nami.common.ContentTypes;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Type;

/**
 * Kryo 解码器
 *
 * @author noear
 * @since 3.0
 */
public class KryoDecoder extends KryoPool implements Decoder {
    public static final KryoDecoder instance = new KryoDecoder();

    @Override
    public String enctype() {
        return label;
    }

    @Override
    public <T> T decode(Result rst, Type clz) {
        if (rst.body().length == 0) {
            return null;
        }

        ByteArrayInputStream inputStream = new ByteArrayInputStream(rst.body());

        Kryo tmp = obtain();

        try (Input input = new Input(inputStream)) {
            return (T)tmp.readClassAndObject(input);
        } finally {
            free(tmp);
        }
    }

    @Override
    public void pretreatment(Context ctx) {
        ctx.headers.put(ContentTypes.HEADER_SERIALIZATION, ContentTypes.AT_KRYO);
        ctx.headers.put(ContentTypes.HEADER_ACCEPT, ContentTypes.KRYO_VALUE);
    }
}

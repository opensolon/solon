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
package org.noear.nami.coder.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import org.noear.nami.Context;
import org.noear.nami.Encoder;

import java.io.ByteArrayOutputStream;

/**
 * Kryo 编码器
 *
 * @author noear
 * @since 3.0
 */
public class KryoEncoder extends KryoPool implements Encoder {
    public static final KryoEncoder instance = new KryoEncoder();

    @Override
    public String enctype() {
        return label;
    }

    @Override
    public byte[] encode(Object obj) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Kryo tmp = obtain();

        try (Output output = new Output(outputStream)) {
            tmp.writeClassAndObject(output, obj);
        } finally {
            free(tmp);
        }

        return outputStream.toByteArray();
    }

    @Override
    public void pretreatment(Context ctx) {

    }
}

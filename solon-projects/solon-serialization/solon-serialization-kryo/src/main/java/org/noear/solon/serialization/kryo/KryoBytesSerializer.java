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
package org.noear.solon.serialization.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ModelAndView;
import org.noear.solon.lang.Nullable;
import org.noear.solon.serialization.ContextSerializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author noear
 * @since  3.0
 */
public class KryoBytesSerializer implements ContextSerializer<byte[]> {
    private static final String label = "application/kryo";
    private final Queue<Kryo> objects = new ConcurrentLinkedQueue<>();

    protected Kryo obtain() {
        Kryo tmp = objects.poll();
        if (tmp == null) {
            tmp = new Kryo();
            tmp.setRegistrationRequired(false);
        }
        return tmp;
    }

    protected void free(Kryo kryo) {
        this.objects.offer(kryo);
    }

    @Override
    public String mimeType() {
        return label;
    }

    @Override
    public Class<byte[]> dataType() {
        return byte[].class;
    }

    @Override
    public byte[] serialize(Object fromObj) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Kryo tmp = obtain();

        try (Output output = new Output(outputStream)) {
            tmp.writeClassAndObject(output, fromObj);
        } finally {
            free(tmp);
        }

        return outputStream.toByteArray();
    }

    @Override
    public Object deserialize(byte[] data, Type toType) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);

        Kryo tmp = obtain();

        try (Input input = new Input(inputStream)) {
            return tmp.readClassAndObject(input);
        } finally {
            free(tmp);
        }
    }

    ///////////////

    @Override
    public boolean matched(Context ctx, String mime) {
        if (mime == null) {
            return false;
        } else {
            return mime.startsWith(label);
        }
    }

    @Override
    public void serializeToBody(Context ctx, Object data) throws IOException {
        ctx.contentType(this.mimeType());

        if (data instanceof ModelAndView) {
            ctx.output(serialize(((ModelAndView) data).model()));
        } else {
            ctx.output(serialize(data));
        }
    }

    @Override
    public Object deserializeFromBody(Context ctx, @Nullable Type bodyType) throws IOException {
        return deserialize(ctx.bodyAsBytes(), null);
    }
}

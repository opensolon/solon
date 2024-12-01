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
package org.noear.solon.serialization.sbe;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ModelAndView;
import org.noear.solon.core.util.ClassUtil;
import org.noear.solon.lang.Nullable;
import org.noear.solon.serialization.ContextSerializer;
import org.noear.solon.serialization.sbe.io.BytesReader;
import org.noear.solon.serialization.sbe.io.BytesSerializable;
import org.noear.solon.serialization.sbe.io.BytesWriter;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author noear
 * @since  3.0
 */
public class SbeBytesSerializer implements ContextSerializer<byte[]> {
    private static final String label = "application/sbe";


    @Override
    public String mimeType() {
        return label;
    }

    @Override
    public Class<byte[]> dataType() {
        return byte[].class;
    }

    @Override
    public boolean bodyRequired() {
        return true;
    }

    @Override
    public byte[] serialize(Object fromObj) throws IOException {
        if (fromObj instanceof BytesSerializable) {
            BytesSerializable bs =  ((BytesSerializable) fromObj);
            BytesWriter bw = bs.serializeFactory().createWriter();
            bs.serializeWrite(bw);
            return bw.toBytes();
//            ExpandableDirectByteBuffer buffer = new ExpandableDirectByteBuffer(128);
//            ((SbeSerializable) fromObj).writeBuffer(new SbeOutputBuffers(buffer));
//
//            byte[] bytes = new byte[buffer.capacity()];
//            buffer.getBytes(0, bytes);
//            return bytes;
        } else {
            throw new IllegalStateException("The parameter 'fromObj' is not of SbeWriteBuffer");
        }
    }

    @Override
    public Object deserialize(byte[] data, Type toType) throws IOException {
        if (toType instanceof Class<?>) {
            if (BytesSerializable.class.isAssignableFrom((Class<?>) toType)) {
                BytesSerializable tmp = ClassUtil.newInstance((Class<?>) toType);
                BytesReader br = tmp.serializeFactory().createReader(data);
                tmp.serializeRead(br);

                return tmp;
            } else {
                throw new IllegalStateException("The parameter 'toType' is not of SbeReadBuffer");
            }
        } else {
            throw new IllegalStateException("The parameter 'toType' is not Class");
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
        return deserialize(ctx.bodyAsBytes(), bodyType);
    }
}

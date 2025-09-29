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
package org.noear.solon.serialization.abc;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ModelAndView;
import org.noear.solon.core.util.ClassUtil;
import org.noear.solon.lang.Nullable;
import org.noear.solon.serialization.EntitySerializer;
import org.noear.solon.serialization.abc.io.AbcSerializable;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author noear
 * @since  3.0
 */
public class AbcBytesSerializer implements EntitySerializer<byte[]> {
    private static final String label = "application/abc";
    private static final AbcBytesSerializer _default = new AbcBytesSerializer();

    /**
     * 默认实例
     */
    public static AbcBytesSerializer getDefault() {
        return _default;
    }

    /**
     * @deprecated 3.6 {@link #getDefault()}
     */
    @Deprecated
    public static AbcBytesSerializer getInstance() {
        return _default;
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
    public boolean bodyRequired() {
        return true;
    }

    /**
     * 序列化器名字
     */
    @Override
    public String name() {
        return "abc-bytes";
    }

    @Override
    public byte[] serialize(Object fromObj) throws IOException {
        if (fromObj instanceof AbcSerializable) {
            AbcSerializable bs = ((AbcSerializable) fromObj);
            Object out = bs.serializeFactory().createOutput();
            bs.serializeWrite(out);
            return bs.serializeFactory().extractBytes(out);
        } else {
            throw new IllegalStateException("The parameter 'fromObj' is not of AbcSerializable");
        }
    }

    @Override
    public Object deserialize(byte[] data, Type toType) throws IOException {
        if (toType instanceof Class<?>) {
            if (AbcSerializable.class.isAssignableFrom((Class<?>) toType)) {
                AbcSerializable tmp = ClassUtil.newInstance((Class<?>) toType);
                Object in = tmp.serializeFactory().createInput(data);
                tmp.serializeRead(in);

                return tmp;
            } else {
                throw new IllegalStateException("The parameter 'toType' is not of AbcSerializable");
            }
        } else {
            throw new IllegalStateException("The parameter 'toType' is not Class");
        }
    }

    /// ////////////

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
        //如果没有设置过，用默认的 //如 ndjson,sse 或故意改变 mime（可由外部控制）
        if (ctx.contentTypeNew() == null) {
            ctx.contentType(this.mimeType());
        }

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
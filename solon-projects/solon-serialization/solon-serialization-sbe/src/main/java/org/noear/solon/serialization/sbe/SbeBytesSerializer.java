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
import org.noear.solon.serialization.ContextSerializer;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author noear
 * @since 3.0
 */
public class SbeBytesSerializer implements ContextSerializer<byte[]> {
    private static final String label = "application/sbe";
    @Override
    public boolean matched(Context ctx, String mime) {
        if (mime == null) {
            return false;
        } else {
            return mime.startsWith(label);
        }
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
        return new byte[0];
    }

    @Override
    public Object deserialize(byte[] data, Type toType) throws IOException {
        return null;
    }

    @Override
    public void serializeToBody(Context ctx, Object data) throws IOException {

    }

    @Override
    public Object deserializeFromBody(Context ctx) throws IOException {
        return null;
    }
}

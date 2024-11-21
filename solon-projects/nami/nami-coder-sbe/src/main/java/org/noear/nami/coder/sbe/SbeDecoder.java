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
package org.noear.nami.coder.sbe;

import org.noear.nami.Context;
import org.noear.nami.Decoder;
import org.noear.nami.Result;
import org.noear.nami.common.Constants;
import org.noear.nami.common.ContentTypes;
import org.noear.solon.serialization.sbe.SbeBytesSerializer;

import java.lang.reflect.Type;

/**
 * @author noear
 * @since 3.0
 */
public class SbeDecoder implements Decoder {
    public static final SbeDecoder instance = new SbeDecoder();

    private final SbeBytesSerializer serializer = new SbeBytesSerializer();

    public SbeBytesSerializer getSerializer() {
        return serializer;
    }

    @Override
    public String enctype() {
        return ContentTypes.SBE_VALUE;
    }

    @Override
    public <T> T decode(Result rst, Type clz) throws Throwable {
        if (rst.body().length == 0) {
            return null;
        }

        return (T) serializer.deserialize(rst.body(), clz);
    }

    @Override
    public void pretreatment(Context ctx) {
        ctx.headers.put(Constants.HEADER_SERIALIZATION, Constants.AT_SBE);
        ctx.headers.put(Constants.HEADER_ACCEPT, ContentTypes.SBE_VALUE);
    }
}

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
package org.noear.nami.coder.snack3;

import org.noear.nami.Context;
import org.noear.nami.Decoder;
import org.noear.nami.EncoderTyped;
import org.noear.nami.Result;
import org.noear.nami.common.ContentTypes;
import org.noear.snack.ONode;
import org.noear.solon.Utils;

import java.lang.reflect.Type;

public class SnackDecoder implements Decoder {
    public static final SnackDecoder instance = new SnackDecoder();

    @Override
    public String enctype() {
        return ContentTypes.JSON_VALUE;
    }

    @Override
    public <T> T decode(Result rst, Type type) {
        if (rst.body().length == 0) {
            return null;
        }

        String str = rst.bodyAsString();
        if ("null".equals(str)) {
            return null;
        }

        if (String.class == type && Utils.isNotEmpty(str)) {
            if (str.charAt(0) != '\'' && str.charAt(0) != '"') {
                return (T) str;
            }
        }

        return ONode.deserialize(str, type);
    }

    @Override
    public void pretreatment(Context ctx) {
        if (ctx.config.getEncoder() instanceof EncoderTyped) {
            ctx.headers.put(ContentTypes.HEADER_SERIALIZATION, ContentTypes.AT_TYPE_JSON);
        }

        ctx.headers.put(ContentTypes.HEADER_ACCEPT, ContentTypes.JSON_VALUE);
    }
}

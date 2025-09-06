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
package org.noear.nami.coder.fastjson2;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import org.noear.nami.*;
import org.noear.nami.common.ContentTypes;
import org.noear.nami.exception.NamiDecodeException;
import org.noear.solon.Utils;

import java.lang.reflect.Type;


/**
 * @author noear
 * @since 1.9
 */
public class Fastjson2Decoder implements Decoder {

    public static final Fastjson2Decoder instance = new Fastjson2Decoder();


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

        try {
            if (str.contains("\"stackTrace\":[{")) {
                return (T) JSON.parseObject(str, Throwable.class, JSONReader.Feature.SupportAutoType);
            } else {
                if (String.class == type && Utils.isNotEmpty(str)) {
                    if (str.charAt(0) != '\'' && str.charAt(0) != '"') {
                        return (T) str;
                    }
                }

                return (T) JSON.parseObject(str, type, JSONReader.Feature.SupportAutoType);
            }
        } catch (Throwable ex) {
            throw new NamiDecodeException("Decoding failure, type: " + type.getTypeName() + ", data: " + str, ex);
        }
    }

    @Override
    public void pretreatment(Context ctx) {
        if (ctx.config.getEncoder() instanceof EncoderTyped) {
            ctx.headers.put(ContentTypes.HEADER_SERIALIZATION, ContentTypes.AT_TYPE_JSON);
        }

        ctx.headers.put(ContentTypes.HEADER_ACCEPT, ContentTypes.JSON_VALUE);
    }
}

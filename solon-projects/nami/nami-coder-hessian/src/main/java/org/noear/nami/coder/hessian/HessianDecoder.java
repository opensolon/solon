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
package org.noear.nami.coder.hessian;

import com.alibaba.com.caucho.hessian.io.Hessian2Input;
import org.noear.nami.Decoder;
import org.noear.nami.Context;
import org.noear.nami.Result;
import org.noear.nami.common.ContentTypes;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Hessian 解码器
 *
 * @author noear
 * @since 1.2
 * */
public class HessianDecoder implements Decoder {
    public static final HessianDecoder instance = new HessianDecoder();

    @Override
    public String enctype() {
        return ContentTypes.HESSIAN_VALUE;
    }


    @Override
    public <T> T decode(Result rst, Type type) throws IOException {
        if (rst.body().length == 0) {
            return null;
        }

        Hessian2Input hi = new Hessian2Input(new ByteArrayInputStream(rst.body()));

        try {
            return (T) hi.readObject();
        } finally {
            hi.close();
        }
    }

    @Override
    public void pretreatment(Context ctx) {
        ctx.headers.put(ContentTypes.HEADER_SERIALIZATION, ContentTypes.AT_HESSIAN);
        ctx.headers.put(ContentTypes.HEADER_ACCEPT, ContentTypes.HESSIAN_VALUE);
    }
}

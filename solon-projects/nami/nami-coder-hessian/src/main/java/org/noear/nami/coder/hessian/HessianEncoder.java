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
package org.noear.nami.coder.hessian;

import com.alibaba.com.caucho.hessian.io.Hessian2Output;
import org.noear.nami.Context;
import org.noear.nami.Encoder;
import org.noear.nami.common.ContentTypes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Hessian 编码器
 *
 * @author noear
 * @since 1.2
 * */
public class HessianEncoder implements Encoder {
    public static final HessianEncoder instance = new HessianEncoder();

    @Override
    public String enctype() {
        return ContentTypes.HESSIAN_VALUE;
    }

    @Override
    public byte[] encode(Object obj) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Hessian2Output ho = new Hessian2Output(out);
        try {
            ho.writeObject(obj);
        } finally {
            ho.close();
        }

        return out.toByteArray();
    }

    @Override
    public void pretreatment(Context ctx) {

    }
}

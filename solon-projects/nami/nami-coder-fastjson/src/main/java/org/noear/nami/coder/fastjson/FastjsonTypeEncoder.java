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
package org.noear.nami.coder.fastjson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.noear.nami.Context;
import org.noear.nami.EncoderTyped;
import org.noear.nami.common.ContentTypes;

import java.nio.charset.StandardCharsets;

/**
 * @deprecated 3.5 不再需要
 * */
@Deprecated
public class FastjsonTypeEncoder implements EncoderTyped {

    public static final FastjsonTypeEncoder instance = new FastjsonTypeEncoder();

    @Override
    public String enctype() {
        return ContentTypes.JSON_TYPE_VALUE;
    }

    @Override
    public byte[] encode(Object obj) {
        return JSON.toJSONString(obj,
                SerializerFeature.BrowserCompatible,
                SerializerFeature.WriteClassName,
                SerializerFeature.DisableCircularReferenceDetect)
                .getBytes(StandardCharsets.UTF_8);

    }

    @Override
    public void pretreatment(Context ctx) {

    }
}

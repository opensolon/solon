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
import org.noear.nami.Encoder;
import org.noear.nami.common.ContentTypes;
import org.noear.solon.serialization.sbe.SbeBytesSerializer;

/**
 * @author noear
 * @since 3.0
 */
public class SbeEncoder implements Encoder {
    public static final SbeEncoder instance = new SbeEncoder();

    private final SbeBytesSerializer serializer = new SbeBytesSerializer();

    public SbeBytesSerializer getSerializer() {
        return serializer;
    }

    @Override
    public boolean bodyRequired() {
        return true;
    }

    @Override
    public String enctype() {
        return ContentTypes.SBE_VALUE;
    }

    @Override
    public byte[] encode(Object obj) throws Throwable {
        return serializer.serialize(obj);
    }

    @Override
    public void pretreatment(Context ctx) {

    }
}
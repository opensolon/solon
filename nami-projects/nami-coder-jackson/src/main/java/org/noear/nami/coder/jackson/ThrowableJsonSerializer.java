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
package org.noear.nami.coder.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * @author noear
 * @since 1.4
 */
public class ThrowableJsonSerializer extends JsonSerializer<Throwable> {

    @Override
    public void serialize(Throwable exception, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream oo = new ObjectOutputStream(byteArrayOutputStream);
        oo.writeObject(exception);
        oo.flush();
        oo.close();
        byte[] bytes = byteArrayOutputStream.toByteArray();
        gen.writeRawValue("\"" + new String(bytes) + "\"");
    }
}

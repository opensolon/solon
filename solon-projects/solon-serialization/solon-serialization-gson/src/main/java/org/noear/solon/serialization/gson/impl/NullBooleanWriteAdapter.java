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
package org.noear.solon.serialization.gson.impl;

import com.google.gson.*;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.noear.solon.serialization.prop.JsonProps;

import java.io.IOException;

public class NullBooleanWriteAdapter extends TypeAdapter<Boolean> {
    private JsonProps jsonProps;

    public NullBooleanWriteAdapter(JsonProps jsonProps) {
        this.jsonProps = jsonProps;
    }

    @Override
    public void write(JsonWriter out, Boolean aBoolean) throws IOException {
        if (jsonProps.boolAsInt) {
            if (aBoolean == null) {
                out.value(0);
            } else {
                out.value(aBoolean ? 1 : 0);
            }
        } else {
            if (aBoolean == null) {
                out.value(false);
            } else {
                out.value(aBoolean);
            }
        }

    }

    @Override
    public Boolean read(JsonReader jsonReader) throws IOException {
        return TypeAdapters.BOOLEAN.read(jsonReader);
    }
}

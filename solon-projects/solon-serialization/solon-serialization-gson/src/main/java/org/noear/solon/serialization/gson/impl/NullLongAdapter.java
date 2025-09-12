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

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.noear.solon.serialization.prop.JsonProps;

import java.io.IOException;
import java.lang.reflect.Type;

public class NullLongAdapter implements JsonSerializer<Long> {
    private JsonProps jsonProps;

    public NullLongAdapter(JsonProps jsonProps) {
        this.jsonProps = jsonProps;
    }


    @Override
    public JsonElement serialize(Long number, Type type, JsonSerializationContext context) {
        if (jsonProps.longAsString) {
            if (number == null) {
                return TypeAdapters.STRING.toJsonTree("0");
            } else {
                return TypeAdapters.STRING.toJsonTree(number.toString());
            }
        } else {
            if (number == null) {
                return TypeAdapters.LONG.toJsonTree(0);
            } else {
                return TypeAdapters.LONG.toJsonTree(number);
            }
        }
    }
}
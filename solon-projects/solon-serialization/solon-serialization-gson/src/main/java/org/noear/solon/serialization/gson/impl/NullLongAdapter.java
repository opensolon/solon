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

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.noear.solon.serialization.prop.JsonProps;

import java.io.IOException;

public class NullLongAdapter extends TypeAdapter<Long> {
    private JsonProps jsonProps;

    public NullLongAdapter(JsonProps jsonProps) {
        this.jsonProps = jsonProps;
    }

    @Override
    public void write(JsonWriter out, Long number) throws IOException {
        if (jsonProps.longAsString) {
            if (number == null) {
                out.value("0");
            } else {
                out.value(number.toString());
            }
        } else {
            if (number == null) {
                out.value(0);
            } else {
                out.value(number);
            }
        }
    }

    @Override
    public Long read(JsonReader jsonReader) throws IOException {
        return null;
    }
}

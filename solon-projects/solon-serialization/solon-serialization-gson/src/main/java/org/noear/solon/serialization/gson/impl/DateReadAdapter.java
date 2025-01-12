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

import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.noear.solon.core.util.DateAnalyzer;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

/**
 * @author noear
 * @since 2.2
 */
public class DateReadAdapter extends TypeAdapter<Date> {
    @Override
    public void write(JsonWriter jsonWriter, Date date) throws IOException {

    }

    @Override
    public Date read(JsonReader in) throws IOException {
        JsonToken jsonToken = in.peek();
        switch (jsonToken) {
            case NULL:
                in.nextNull();
                return null;
            case NUMBER:
                return new Date(in.nextLong());
            case STRING:
                try {
                    return DateAnalyzer.global().parse(in.nextString());
                } catch (ParseException e) {
                    throw new JsonSyntaxException("Expecting date, got: " + jsonToken + "; at path " + in.getPath(), e);
                }
            default:
                throw new JsonSyntaxException("Expecting date, got: " + jsonToken + "; at path " + in.getPath());
        }
    }
}

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
import org.noear.solon.core.util.DateAnalyzer;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.Date;

/**
 * @author noear
 * @since 2.2
 */
public class DateDeserializer implements JsonDeserializer<Date> {
    @Override
    public Date deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        if (jsonElement.isJsonNull()) {
            return null;
        } else if (jsonElement.isJsonPrimitive()) {
            JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();

            if (jsonPrimitive.isNumber()) {
                return new Date(jsonPrimitive.getAsNumber().longValue());
            } else if (jsonPrimitive.isString()) {
                try {
                    return DateAnalyzer.global().parse(jsonPrimitive.getAsString());
                } catch (ParseException e) {
                    throw new JsonSyntaxException("Expecting date, by: " + jsonElement, e);
                }
            }
        }

        throw new JsonSyntaxException("Expecting date, by: " + jsonElement);
    }
}
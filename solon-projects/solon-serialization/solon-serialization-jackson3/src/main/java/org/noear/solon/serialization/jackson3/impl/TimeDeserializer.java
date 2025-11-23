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
package org.noear.solon.serialization.jackson3.impl;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import org.noear.solon.core.util.DateUtil;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

/**
 * 时间反序列化器
 *
 * @author noear
 * @since 3.5
 */
public class TimeDeserializer<T> extends ValueDeserializer<T> {
    private final Class<T> type;

    public TimeDeserializer(Class<T> type) {
        this.type = type;
    }

    @Override
    public T deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
    		throws JacksonException {
    	String val = jsonParser.getValueAsString();

        try {
            Date date = DateUtil.parse(val);

            if(date == null) {
                return null;
            }

            if (type == LocalDateTime.class) {
                return (T) LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
            } else if (type == LocalDate.class) {
                return (T) LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).toLocalDate();
            } else if (type == LocalTime.class) {
                return (T) LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).toLocalTime();
            } else if (type == ZonedDateTime.class) {
                return (T) ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
            } else if (type == OffsetDateTime.class) {
                return (T) OffsetDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
            } else if (type == OffsetTime.class) {
                return (T) OffsetTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
            } else if (type == Instant.class) {
                return (T) date.toInstant();
            } else if (type == Date.class) {
                return (T) date;
            } else {
                return null;
            }

        } catch (Exception ex) {
            throw  JacksonException.wrapWithPath(ex,jsonParser, type.getSimpleName() + " parse fail: '" + val + "'");
        }
    }
    
}

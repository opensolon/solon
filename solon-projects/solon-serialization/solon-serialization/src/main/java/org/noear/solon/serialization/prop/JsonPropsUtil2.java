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
package org.noear.solon.serialization.prop;

import org.noear.solon.Utils;
import org.noear.solon.serialization.EntityStringSerializer;
import org.noear.solon.serialization.JsonRenderFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author noear
 * @since 1.12
 * @since 3.6
 */
public class JsonPropsUtil2 {
    /**
     * 尝试应用 Json 全局配置
     *
     * @deprecated 3.2 {@link #dateAsFormat(EntityStringSerializer, JsonProps),#dateAsTicks(JsonRenderFactory, JsonProps),#boolAsInt(JsonRenderFactory, JsonProps)}
     */
    @Deprecated
    public static boolean apply(EntityStringSerializer serializer, JsonProps jsonProps) {
        if (jsonProps == null) {
            return false;
        }

        dateAsFormat(serializer, jsonProps);
        dateAsTicks(serializer, jsonProps);
        boolAsInt(serializer, jsonProps);

        return true;
    }


    /**
     * 尝试把 bool 转为 int
     */
    public static void boolAsInt(EntityStringSerializer serializer, JsonProps jsonProps) {
        if (jsonProps == null) {
            return;
        }

        if (jsonProps.boolAsInt) {
            serializer.addEncoder(Boolean.class, e -> (e ? 1 : 0));
            serializer.addEncoder(boolean.class, e -> (e ? 1 : 0));
        }
    }

    /**
     * 尝试把 long 转为 string
     */
    public static void longAsString(EntityStringSerializer serializer, JsonProps jsonProps) {
        if (jsonProps == null) {
            return;
        }

        if (jsonProps.longAsString) {
            serializer.addEncoder(Long.class, String::valueOf);
            serializer.addEncoder(long.class, String::valueOf);
        }
    }

    /**
     * 尝试把 date 转为 ticks(long)
     */
    public static void dateAsTicks(EntityStringSerializer serializer, JsonProps jsonProps) {
        if (jsonProps == null) {
            return;
        }

        if (jsonProps.dateAsTicks) {
            serializer.addEncoder(Date.class, d -> {
                if (jsonProps.longAsString) {
                    return String.valueOf(d.getTime());
                } else {
                    return d.getTime();
                }
            });
        }
    }

    public static void dateAsFormat(EntityStringSerializer serializer, JsonProps jsonProps) {
        if (jsonProps == null) {
            return;
        }

        if (Utils.isNotEmpty(jsonProps.dateAsFormat)) {
            serializer.addEncoder(Date.class, e -> {
                DateFormat df = new SimpleDateFormat(jsonProps.dateAsFormat);

                if (Utils.isNotEmpty(jsonProps.dateAsTimeZone)) {
                    df.setTimeZone(TimeZone.getTimeZone(ZoneId.of(jsonProps.dateAsTimeZone)));
                }

                return df.format(e);
            });

            serializer.addEncoder(OffsetDateTime.class, e -> {
                DateTimeFormatter df = DateTimeFormatter.ofPattern(jsonProps.dateAsFormat);
                if (Utils.isNotEmpty(jsonProps.dateAsTimeZone)) {
                    df.withZone(ZoneId.of(jsonProps.dateAsTimeZone));
                }

                return e.format(df);
            });

            serializer.addEncoder(ZonedDateTime.class, e -> {
                DateTimeFormatter df = DateTimeFormatter.ofPattern(jsonProps.dateAsFormat);
                if (Utils.isNotEmpty(jsonProps.dateAsTimeZone)) {
                    df.withZone(ZoneId.of(jsonProps.dateAsTimeZone));
                }

                return e.format(df);
            });

            serializer.addEncoder(LocalDateTime.class, e -> formatLocalDateTime(e, jsonProps));

            serializer.addEncoder(LocalDate.class, e -> formatLocalDateTime(e.atStartOfDay(), jsonProps));
        }
    }

    /// //////

    /**
     * 格式化 LocalDateTime
     */
    private static String formatLocalDateTime(LocalDateTime e, JsonProps jsonProps) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern(jsonProps.dateAsFormat);

        if (Utils.isNotEmpty(jsonProps.dateAsTimeZone)) {
            df.withZone(ZoneId.of(jsonProps.dateAsTimeZone));
        }

        return e.format(df);
    }
}
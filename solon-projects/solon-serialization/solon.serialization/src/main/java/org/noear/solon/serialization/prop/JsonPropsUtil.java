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
package org.noear.solon.serialization.prop;

import org.noear.solon.Utils;
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
 */
public class JsonPropsUtil {
    public static boolean apply(JsonRenderFactory factory, JsonProps jsonProps) {
        if (jsonProps == null) {
            return false;
        }

        if (Utils.isNotEmpty(jsonProps.dateAsFormat)) {
            factory.addConvertor(Date.class, e -> {
                DateFormat df = new SimpleDateFormat(jsonProps.dateAsFormat);

                if (Utils.isNotEmpty(jsonProps.dateAsTimeZone)) {
                    df.setTimeZone(TimeZone.getTimeZone(ZoneId.of(jsonProps.dateAsTimeZone)));
                }

                return df.format(e);
            });

            factory.addConvertor(OffsetDateTime.class, e -> {
                DateTimeFormatter df = DateTimeFormatter.ofPattern(jsonProps.dateAsFormat);
                if (Utils.isNotEmpty(jsonProps.dateAsTimeZone)) {
                    df.withZone(ZoneId.of(jsonProps.dateAsTimeZone));
                }

                return e.format(df);
            });

            factory.addConvertor(ZonedDateTime.class, e -> {
                DateTimeFormatter df = DateTimeFormatter.ofPattern(jsonProps.dateAsFormat);
                if (Utils.isNotEmpty(jsonProps.dateAsTimeZone)) {
                    df.withZone(ZoneId.of(jsonProps.dateAsTimeZone));
                }

                return e.format(df);
            });

            factory.addConvertor(LocalDateTime.class,e->{
                DateTimeFormatter df = DateTimeFormatter.ofPattern(jsonProps.dateAsFormat);

                if (Utils.isNotEmpty(jsonProps.dateAsTimeZone)) {
                    df.withZone(ZoneId.of(jsonProps.dateAsTimeZone));
                }

                return e.format(df);
            });

            factory.addConvertor(LocalDate.class, e->{
                DateTimeFormatter df = DateTimeFormatter.ofPattern(jsonProps.dateAsFormat);

                //对该类型格式化为1.抛弃时分秒(拼接00:00:00) 2.格式化为当前时分秒,当前默认采用第一种
                LocalDateTime forTime = e.atStartOfDay();

                if (Utils.isNotEmpty(jsonProps.dateAsTimeZone)) {
                    df.withZone(ZoneId.of(jsonProps.dateAsTimeZone));
                }

                return forTime.format(df);
            });
        }

        if(jsonProps.dateAsTicks){
            factory.addConvertor(Date.class, Date::getTime);
        }

        if (jsonProps.boolAsInt) {
            factory.addConvertor(Boolean.class, e -> (e ? 1 : 0));
        }

        return true;
    }
}

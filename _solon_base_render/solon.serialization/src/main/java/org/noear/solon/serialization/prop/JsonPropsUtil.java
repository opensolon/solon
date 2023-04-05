package org.noear.solon.serialization.prop;

import org.noear.solon.Utils;
import org.noear.solon.serialization.JsonRenderFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
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

            factory.addConvertor(LocalDateTime.class,e->{
                DateTimeFormatter df = DateTimeFormatter.ofPattern(jsonProps.dateAsFormat);

                if (Utils.isNotEmpty(jsonProps.dateAsTimeZone)) {
                    df.withZone(ZoneId.of(jsonProps.dateAsTimeZone));
                }

                return e.format(df);
            });

            factory.addConvertor(LocalDate.class, e->{
                DateTimeFormatter df = DateTimeFormatter.ofPattern(jsonProps.dateAsFormat);

                if (Utils.isNotEmpty(jsonProps.dateAsTimeZone)) {
                    df.withZone(ZoneId.of(jsonProps.dateAsTimeZone));
                }

                return e.format(df);
            });
        }

        if(jsonProps.dateAsTicks){
            factory.addConvertor(Date.class, e -> e.getTime());
        }

        if (jsonProps.longAsString) {
            factory.addConvertor(Long.class, e -> String.valueOf(e));
        }

        if (jsonProps.boolAsInt) {
            factory.addConvertor(Boolean.class, e -> (e ? 1 : 0));
        }

        return true;
    }
}

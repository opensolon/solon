package org.noear.solon.serialization.prop;

import org.noear.solon.Utils;
import org.noear.solon.serialization.JsonRenderFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author noear
 * @since 1.12
 */
public class JsonPropsUtil {
    public static void apply(JsonRenderFactory factory, JsonProps jsonProps) {
        if (jsonProps == null) {
            return;
        }

        if (Utils.isNotEmpty(jsonProps.dateAsFormat)) {
            factory.addConvertor(Date.class, e -> {
                DateFormat df = new SimpleDateFormat(jsonProps.dateAsFormat);

                if (Utils.isNotEmpty(jsonProps.dateAsTimeZone)) {
                    df.setTimeZone(TimeZone.getTimeZone(jsonProps.dateAsTimeZone));
                }

                return df.format(e);
            });
        }

        if (jsonProps.longAsString) {
            factory.addConvertor(Long.class, e -> String.valueOf(e));
        }

        if (jsonProps.intAsString) {
            factory.addConvertor(Integer.class, e -> String.valueOf(e));
        }

        if (jsonProps.boolAsInt) {
            factory.addConvertor(Boolean.class, e -> (e ? 1 : 0));
        }
    }
}

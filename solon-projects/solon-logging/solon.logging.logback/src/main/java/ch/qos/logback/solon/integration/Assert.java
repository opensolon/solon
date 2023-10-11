package ch.qos.logback.solon.integration;

import org.noear.solon.Utils;
import org.noear.solon.lang.Nullable;

/**
 * @author noear
 * @since 2.5
 */
public class Assert {
    public static void notNull(@Nullable Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void hasLength(@Nullable String text, String message) {
        if (Utils.isEmpty(text)) {
            throw new IllegalArgumentException(message);
        }
    }
}

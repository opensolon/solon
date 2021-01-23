package org.noear.mlog;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 添加器简易版
 *
 * @author noear
 * @since 1.2
 */
public class AppenderSimple implements Appender {
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void append(Level level, Marker marker, Object content) {
        String text = null;

        if (marker == null) {
            text = String.format("%s [%s] :: %s",
                    sdf.format(new Date()),
                    level.name(),
                    content);
        } else {
            text = String.format("%s [%s] %s:: %s",
                    sdf.format(new Date()),
                    level.name(),
                    marker.formatAsString(),
                    content);
        }

        System.out.println(text);
    }
}

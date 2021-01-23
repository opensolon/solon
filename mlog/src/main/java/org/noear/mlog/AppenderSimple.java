package org.noear.mlog;

/**
 * 添加器简易版
 *
 * @author noear
 * @since 1.2
 */
public class AppenderSimple implements Appender {
    @Override
    public void append(Level level, Marker meta, Object content) {
        StringBuilder sb = new StringBuilder();

        sb.append("[").append(level.name()).append("] ")
                .append(meta.formatAsString())
                .append(content);

        System.out.println(sb.toString());
    }
}

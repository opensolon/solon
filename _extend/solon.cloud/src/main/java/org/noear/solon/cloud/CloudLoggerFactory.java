package org.noear.solon.cloud;

import org.noear.solon.cloud.model.log.Level;

/**
 * @author noear
 * @since 1.2
 */
public class CloudLoggerFactory {
    //
    //日志等级
    //
    private static volatile Level level = Level.TRACE;

    public static void setLevel(Level level) {
        CloudLoggerFactory.level = level;
    }

    public static Level getLevel() {
        return CloudLoggerFactory.level;
    }

}

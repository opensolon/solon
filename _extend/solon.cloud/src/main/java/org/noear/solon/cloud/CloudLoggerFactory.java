package org.noear.solon.cloud;

import org.noear.solon.cloud.model.log.Level;

/**
 * @author noear
 * @since 1.2
 */
public enum CloudLoggerFactory {
    INSTANCE;

    //
    //日志等级
    //
    private volatile Level level = Level.TRACE;

    public void setLevel(Level level) {
        this.level = level;
    }

    public Level getLevel() {
        return this.level;
    }

}

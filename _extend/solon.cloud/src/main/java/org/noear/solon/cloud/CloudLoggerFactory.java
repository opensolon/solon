package org.noear.solon.cloud;

import org.noear.solon.cloud.model.log.Level;

/**
 * @author noear 2021/1/22 created
 */
public enum CloudLoggerFactory {
    INSTANCE;

    //
    //日志等级
    //
    private volatile Level level = Level.WARN;

    public void setLevel(Level level) {
        this.level = level;
    }

    public Level getLevel() {
        return this.level;
    }

}

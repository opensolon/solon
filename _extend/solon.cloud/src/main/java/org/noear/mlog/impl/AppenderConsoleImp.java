package org.noear.mlog.impl;

import org.noear.mlog.Level;
import org.noear.mlog.Metainfo;
import org.noear.solon.Solon;
import org.noear.solon.cloud.CloudLogAppender;
import org.noear.solon.cloud.impl.CloudLogAppenderSimple;

/**
 * @author noear
 * @since 1.3
 */
public class AppenderConsoleImp extends CloudLogAppenderSimple {

    @Override
    public String getName() {
        return "console";
    }

    @Override
    protected Level levelDefault() {
        return Level.TRACE;
    }

    @Override
    protected void appendDo(String loggerName, Class<?> clz, Level level, Metainfo metainfo, Object content) {
        if (Solon.cfg().isDebugMode() || Solon.cfg().isFilesMode()) {
            super.appendDo(loggerName, clz, level, metainfo, content);
        }
    }
}

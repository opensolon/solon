package org.noear.solon.logging;

import org.noear.mlog.Level;
import org.noear.mlog.Metainfo;
import org.noear.snack.ONode;
import org.noear.solon.Solon;

/**
 * @author noear
 * @since 1.3
 */
public class LogConsoleAppender extends LogAbstractAppender {

    @Override
    public String getName() {
        return "console";
    }

    @Override
    public Level getDefaultLevel() {
        return Level.TRACE;
    }

    @Override
    protected void appendDo(String loggerName, Class<?> clz, Level level, Metainfo metainfo, Object content) {
        if (Solon.cfg().isDebugMode() || Solon.cfg().isFilesMode()) {
            super.appendDo(loggerName, clz, level, metainfo, content);
        }
    }

    @Override
    protected void appendContentDo(Object content) {
        if (content instanceof String) {
            System.out.println(content);
        } else {
            System.out.println(ONode.stringify(content));
        }
    }
}

package org.noear.mlog.impl;

import org.noear.mlog.Level;
import org.noear.solon.cloud.impl.CloudAppenderSimple;

/**
 * @author noear
 * @since 1.3
 */
public class AppenderConsoleImp extends CloudAppenderSimple {

    @Override
    public String getName() {
        return "console";
    }

    @Override
    protected Level levelDefault() {
        return Level.TRACE;
    }
}

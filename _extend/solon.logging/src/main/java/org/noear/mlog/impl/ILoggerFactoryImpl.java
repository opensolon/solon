package org.noear.mlog.impl;

import org.noear.logging.AppenderManager;
import org.noear.mlog.Appender;
import org.noear.mlog.ILoggerFactory;
import org.noear.mlog.Logger;
import org.noear.mlog.LoggerSimple;

/**
 * @author noear
 * @since 1.2
 */
public class ILoggerFactoryImpl implements ILoggerFactory {
    @Override
    public Logger getLogger(String name) {
        return new LoggerSimple(name);
    }

    @Override
    public Logger getLogger(Class<?> clz) {
        return new LoggerSimple(clz);
    }

    @Override
    public Appender getAppender() {
        return AppenderManager.getInstance();
    }
}

package org.noear.mlog.impl;

import org.noear.mlog.Appender;
import org.noear.mlog.ILoggerFactory;
import org.noear.mlog.Logger;

/**
 * @author noear
 * @since 1.2
 */
public class ILoggerFactoryImpl implements ILoggerFactory {
    @Override
    public Logger getLogger(String name) {
        return new LoggerWaterImp(name);
    }

    @Override
    public Logger getLogger(Class<?> clz) {
        return new LoggerWaterImp(clz);
    }

    @Override
    public Appender getAppender() {
        return AppenderProxy.getInstance();
    }
}

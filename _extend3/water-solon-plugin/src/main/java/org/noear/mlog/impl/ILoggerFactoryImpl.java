package org.noear.mlog.impl;

import org.noear.mlog.ILoggerFactory;
import org.noear.mlog.Logger;
import org.noear.solon.cloud.extend.water.service.CloudLoggerImp;

/**
 * @author noear
 * @since 1.2
 */
public class ILoggerFactoryImpl implements ILoggerFactory {
    @Override
    public Logger getLogger(String name) {
        return new CloudLoggerImp(name);
    }

    @Override
    public Logger getLogger(Class<?> clz) {
        return new CloudLoggerImp(clz);
    }
}

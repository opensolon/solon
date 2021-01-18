package org.slf4j.impl;

import org.noear.solon.Solon;
import org.noear.solon.cloud.CloudLogger;
import org.noear.solon.cloud.model.Level;

/**
 * @author noear
 * @since 1.2
 * */
public class Slf4jCloudWriterImp implements Slf4jCloudWriter {
    CloudLogger logger;

    public Slf4jCloudWriterImp() {
        String name = Solon.cfg().appGroup() + "_" + Solon.cfg().appName() + "_log";
        logger = CloudLogger.get(name);
    }

    @Override
    public void write(Level level, String name, String content) {
        logger.write(level, name, content);
    }
}

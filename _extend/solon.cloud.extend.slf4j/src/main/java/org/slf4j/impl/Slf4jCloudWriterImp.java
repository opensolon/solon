package org.slf4j.impl;

import org.noear.solon.Solon;
import org.noear.solon.cloud.CloudLogger;
import org.slf4j.event.Level;

/**
 * @author noear
 * @since 1.2
 * */
public class Slf4jCloudWriterImp implements Slf4jCloudWriter {
    CloudLogger logger;

    private void init(){
        if (logger == null) {
            synchronized (this) {
                if (logger == null) {
                    String tmp = Solon.cfg().appGroup() + "_" + Solon.cfg().appName() + "_log";
                    logger = CloudLogger.get(tmp);
                }
            }
        }
    }

    @Override
    public void write(Level level, String name, String content) {
        init();

        switch (level) {
            case TRACE:
                logger.trace(name, content);
                break;
            case DEBUG:
                logger.debug(name, content);
                break;
            case WARN:
                logger.warn(name, content);
                break;
            case ERROR:
                logger.error(name, content);
                break;
            default:
                logger.info(name, content);
                break;
        }
    }
}

package org.slf4j.impl;

import org.noear.solon.Solon;
import org.noear.solon.cloud.CloudLogger;
import org.noear.solon.cloud.model.log.Meta;
import org.slf4j.event.Level;

/**
 * @author noear
 * @since 1.2
 * */
public class Slf4jCloudWriterImp implements Slf4jCloudWriter {
    CloudLogger logger;

    private void init() {
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

        Meta meta = new Meta().tag3(name);

        switch (level) {
            case TRACE:
                logger.trace(meta, content);
                break;
            case DEBUG:
                logger.debug(meta, content);
                break;
            case WARN:
                logger.warn(meta, content);
                break;
            case ERROR:
                logger.error(meta, content);
                break;
            default:
                logger.info(meta, content);
                break;
        }
    }
}

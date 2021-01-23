package org.slf4j.impl;

import org.noear.solon.cloud.CloudLogger;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.model.log.Meta;
import org.noear.solon.core.handle.Context;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.event.Level;

/**
 * @author noear
 * @since 1.2
 */
public enum Slf4jCloudLoggerFactory implements ILoggerFactory {
    /**
     * 工厂单例
     */
    INSTANCE;


    /**
     * 日志等级（INFO 内容太多了）
     */
    private volatile Level level = Level.WARN;


    Slf4jCloudLoggerFactory() {

    }

    @Override
    public Logger getLogger(String name) {
        return new Slf4jCloudLogger(name);
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public Level getLevel() {
        return this.level;
    }


    CloudLogger logger;

    public void write(Level level, String name, String content) {
        if (logger == null) {
            synchronized (this) {
                if (logger == null) {
                    logger = CloudLogger.get(CloudProps.LOG_DEFAULT_LOGGER);
                }
            }
        }

        Context ctx = Context.current();
        Meta meta = new Meta().tag1("slf4j").tag2(name);
        if (ctx != null) {
            meta.tag3(ctx.path());
        }

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

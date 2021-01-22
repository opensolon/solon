package org.noear.solon.cloud.extend.water.service;

import org.noear.solon.cloud.CloudLogger;
import org.noear.solon.cloud.model.log.Meta;
import org.noear.water.log.WaterLogger;

/**
 * @author noear
 * @since 1.2
 */
public class CloudLoggerImp implements CloudLogger {
    private WaterLogger logger;
    public CloudLoggerImp(String name) {
        logger = new WaterLogger(name);
    }

    public CloudLoggerImp(String name, Class<?> clz) {
        logger = new WaterLogger(name, clz);
    }

    @Override
    public String getName() {
        return logger.getName();
    }

    @Override
    public void setName(String name) {
        logger.setName(name);
    }



    @Override
    public void trace(Object content) {
        logger.trace(content);
    }

    @Override
    public void trace(Meta meta, Object content) {
        logger.trace(meta.tag1(), meta.tag2(), meta.tag3(), meta.tag4(), meta.summary(), content);
    }

    @Override
    public void debug(Object content) {
        logger.debug(content);
    }

    @Override
    public void debug(Meta meta, Object content) {
        logger.debug(meta.tag1(), meta.tag2(), meta.tag3(), meta.tag4(), meta.summary(), content);
    }

    @Override
    public void info(Object content) {
        logger.info(content);
    }

    @Override
    public void info(Meta meta, Object content) {
        logger.info(meta.tag1(), meta.tag2(), meta.tag3(), meta.tag4(), meta.summary(), content);
    }

    @Override
    public void warn(Object content) {
        logger.warn(content);
    }

    @Override
    public void warn(Meta meta, Object content) {
        logger.warn(meta.tag1(), meta.tag2(), meta.tag3(), meta.tag4(), meta.summary(), content);
    }

    @Override
    public void error(Object content) {
        logger.error(content);
    }

    @Override
    public void error(Meta meta, Object content) {
        logger.error(meta.tag1(), meta.tag2(), meta.tag3(), meta.tag4(), meta.summary(), content);
    }
}

package org.noear.solon.cloud.extend.water.service;

import org.noear.solon.cloud.CloudLogger;
import org.noear.solon.cloud.model.log.Level;
import org.noear.solon.cloud.model.log.Meta;
import org.noear.water.log.WaterLogger;

/**
 * @author noear
 * @since 1.2
 */
public class CloudLoggerImp implements CloudLogger {
    private WaterLogger logger;
    private String name;
    private Class<?> clz;

    public CloudLoggerImp(String name, Class<?> clz) {
        this.name = name;
        this.clz = clz;

        this.logger = new WaterLogger(name, clz);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
        logger.setName(name);
    }



    @Override
    public void trace(Object content) {
        traceDo(null, null,null,null,null, content);
    }

    @Override
    public void trace(String tag1, Object content) {
        traceDo(tag1, null,null,null,null, content);
    }

    @Override
    public void trace(String tag1, String tag2, Object content) {
        traceDo(tag1, tag2,null,null,null, content);
    }

    @Override
    public void trace(Meta meta, Object content) {
        traceDo(meta.tag1(), meta.tag2(), meta.tag3(), meta.tag4(), meta.summary(), content);
    }

    private void traceDo(String tag1, String tag2, String tag3, String tag4, String summary, Object content) {
        if (this.isTraceEnabled()) {
            this.append(Level.TRACE, tag1, tag2, tag3, tag4, summary, content);
        }
    }



    @Override
    public void debug(Object content) {
        debugDo(null, null,null,null,null, content);
    }

    @Override
    public void debug(String tag1, Object content) {
        debugDo(tag1, null,null,null,null, content);
    }

    @Override
    public void debug(String tag1, String tag2, Object content) {
        debugDo(tag1, tag2,null,null,null, content);
    }

    @Override
    public void debug(Meta meta, Object content) {
        debugDo(meta.tag1(), meta.tag2(), meta.tag3(), meta.tag4(), meta.summary(), content);
    }

    private void debugDo(String tag1, String tag2, String tag3, String tag4, String summary, Object content) {
        if (this.isDebugEnabled()) {
            this.append(Level.DEBUG, tag1, tag2, tag3, tag4, summary, content);
        }
    }



    @Override
    public void info(Object content) {
        infoDo(null, null,null,null,null, content);
    }

    @Override
    public void info(String tag1, Object content) {
        infoDo(tag1, null,null,null,null, content);
    }

    @Override
    public void info(String tag1, String tag2, Object content) {
        infoDo(tag1, tag2,null,null,null, content);
    }

    @Override
    public void info(Meta meta, Object content) {
        infoDo(meta.tag1(), meta.tag2(), meta.tag3(), meta.tag4(), meta.summary(), content);
    }

    private void infoDo(String tag1, String tag2, String tag3, String tag4, String summary, Object content) {
        if (this.isInfoEnabled()) {
            this.append(Level.INFO, tag1, tag2, tag3, tag4, summary, content);
        }
    }


    @Override
    public void warn(Object content) {
        warnDo(null, null,null,null,null, content);
    }

    @Override
    public void warn(String tag1, Object content) {
        warnDo(tag1, null,null,null,null, content);
    }

    @Override
    public void warn(String tag1, String tag2, Object content) {
        warnDo(tag1, tag2,null,null,null, content);
    }

    @Override
    public void warn(Meta meta, Object content) {
        warnDo(meta.tag1(), meta.tag2(), meta.tag3(), meta.tag4(), meta.summary(), content);
    }

    private void warnDo(String tag1, String tag2, String tag3, String tag4, String summary, Object content) {
        if (this.isWarnEnabled()) {
            this.append(Level.WARN, tag1, tag2, tag3, tag4, summary, content);
        }
    }


    @Override
    public void error(Object content) {
        errorDo(null, null,null,null,null, content);
    }

    @Override
    public void error(String tag1, Object content) {
        errorDo(tag1, null,null,null,null, content);
    }

    @Override
    public void error(String tag1, String tag2, Object content) {
        errorDo(tag1, tag2,null,null,null, content);
    }

    @Override
    public void error(Meta meta, Object content) {
        errorDo(meta.tag1(), meta.tag2(), meta.tag3(), meta.tag4(), meta.summary(), content);
    }

    private void errorDo(String tag1, String tag2, String tag3, String tag4, String summary, Object content) {
        if (this.isErrorEnabled()) {
            this.append(Level.ERROR, tag1, tag2, tag3, tag4, summary, content);
        }
    }

    public void append(Level level, String tag, String tag1, String tag2, String tag3, String summary, Object content) {
        logger.append(org.noear.water.log.Level.of(level.code), tag, tag1, tag2, tag3, summary, content);
    }
}

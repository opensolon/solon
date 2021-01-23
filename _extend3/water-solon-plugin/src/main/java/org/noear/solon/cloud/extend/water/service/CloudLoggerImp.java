package org.noear.solon.cloud.extend.water.service;

import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudLogger;
import org.noear.solon.cloud.model.log.Level;
import org.noear.solon.cloud.model.log.Meta;
import org.noear.solon.cloud.utils.log.MessageFormatter;
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
        traceDo(null, content, null, null);
    }

    @Override
    public void trace(String format, Object[] args) {
        traceDo(null, null, format, args);
    }

    @Override
    public void trace(Meta meta, Object content) {
        traceDo(meta, content, null, null);
    }

    @Override
    public void trace(Meta meta, String format, Object[] args) {
        traceDo(meta, null, format, args);
    }

    private void traceDo(Meta meta, Object content, String format, Object[] args) {
        if (this.isTraceEnabled()) {
            this.append(Level.TRACE, meta, content, format, args);
        }
    }


    @Override
    public void debug(Object content) {
        debugDo(null, content, null, null);
    }

    @Override
    public void debug(String format, Object[] args) {
        debugDo(null, null, format, args);
    }

    @Override
    public void debug(Meta meta, Object content) {
        debugDo(meta, content, null, null);
    }

    @Override
    public void debug(Meta meta, String format, Object[] args) {
        debugDo(meta, null, format, args);
    }

    private void debugDo(Meta meta, Object content, String format, Object[] args) {
        if (this.isDebugEnabled()) {
            this.append(Level.DEBUG, meta, content, format, args);
        }
    }


    @Override
    public void info(Object content) {
        infoDo(null, content, null, null);
    }

    @Override
    public void info(String format, Object[] args) {
        infoDo(null, null, format, args);
    }

    @Override
    public void info(Meta meta, Object content) {
        infoDo(meta, content, null, null);
    }

    @Override
    public void info(Meta meta, String format, Object[] args) {
        infoDo(meta, null, format, args);
    }

    private void infoDo(Meta meta, Object content, String format, Object[] args) {
        if (this.isInfoEnabled()) {
            this.append(Level.INFO, meta, content, format, args);
        }
    }


    @Override
    public void warn(Object content) {
        warnDo(null, content, null, null);
    }

    @Override
    public void warn(String format, Object[] args) {
        warnDo(null, null, format, args);
    }

    @Override
    public void warn(Meta meta, Object content) {
        warnDo(meta, content, null, null);
    }

    @Override
    public void warn(Meta meta, String format, Object[] args) {
        warnDo(meta, null, format, args);
    }

    private void warnDo(Meta meta, Object content, String format, Object[] args) {
        if (this.isWarnEnabled()) {
            this.append(Level.WARN, meta, content, format, args);
        }
    }


    @Override
    public void error(Object content) {
        errorDo(null, content, null, null);
    }

    @Override
    public void error(String format, Object[] args) {
        errorDo(null, null, format, args);
    }

    @Override
    public void error(Meta meta, Object content) {
        errorDo(meta, content, null, null);
    }

    @Override
    public void error(Meta meta, String format, Object[] args) {
        errorDo(meta, null, format, args);
    }

    private void errorDo(Meta meta, Object content, String format, Object[] args) {
        if (this.isErrorEnabled()) {
            this.append(Level.ERROR, meta, content, format, args);
        }
    }


    private void append(Level level, Meta meta, Object content, String format, Object[] args) {
        //logger.append(org.noear.water.log.Level.of(level.code), tag, tag1, tag2, tag3, summary, content);
        if (Utils.isNotEmpty(format)) {
            content = MessageFormatter.arrayFormat(format, args);
        }

        write(level, meta, content);
    }

    public void write(Level level, Meta meta, Object content) {

    }
}

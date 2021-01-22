package org.noear.solon.cloud;

import org.noear.solon.Utils;
import org.noear.solon.cloud.model.log.Level;
import org.noear.solon.cloud.model.log.Meta;

/**
 * 云日志器
 *
 * @author noear
 * @since 1.2
 */
public interface CloudLogger {
    //
    //获取日志器
    //
    static CloudLogger get(String name) {
        return get(name, null);
    }

    static CloudLogger get(Class<?> clz) {
        return get(CloudProps.LOG_DEFAULT_LOGGER, clz);
    }

    static CloudLogger get(String name, Class<?> clz) {
        if (CloudClient.log() == null || Utils.isEmpty(name)) {
            return CloudLoggerDefault.instance;
        } else {
            return CloudClient.log().getLogger(name, clz);
        }
    }


    String getName();

    void setName(String name);



    default boolean isTraceEnabled() {
        return CloudLoggerFactory.INSTANCE.getLevel().code <= Level.TRACE.code;
    }

    void trace(Object content);
    void trace(String tag1, Object content);
    void trace(String tag1, String tag2, Object content);
    void trace(Meta meta, Object content);



    default boolean isDebugEnabled() {
        return CloudLoggerFactory.INSTANCE.getLevel().code <= Level.DEBUG.code;
    }

    void debug(Object content);
    void debug(String tag1, Object content);
    void debug(String tag1, String tag2, Object content);
    void debug(Meta meta, Object content);



    default boolean isInfoEnabled() {
        return CloudLoggerFactory.INSTANCE.getLevel().code <= Level.INFO.code;
    }

    void info(Object content);
    void info(String tag1, Object content);
    void info(String tag1, String tag2, Object content);
    void info(Meta meta, Object content);



    default boolean isWarnEnabled() {
        return CloudLoggerFactory.INSTANCE.getLevel().code <= Level.WARN.code;
    }

    void warn(Object content);
    void warn(String tag1, Object content);
    void warn(String tag1, String tag2, Object content);
    void warn(Meta meta, Object content);


    default boolean isErrorEnabled() {
        return CloudLoggerFactory.INSTANCE.getLevel().code <= Level.ERROR.code;
    }

    void error(Object content);
    void error(String tag1, Object content);
    void error(String tag1, String tag2, Object content);
    void error(Meta meta, Object content);
}

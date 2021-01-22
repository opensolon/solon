package org.noear.solon.cloud;

import org.noear.solon.Utils;
import org.noear.solon.cloud.model.log.Meta;

/**
 * 云日志器
 *
 * @author noear
 * @since 1.2
 */
public interface CloudLogger {
    static CloudLogger get(String name) {
        if (CloudClient.log() == null) {
            return CloudLoggerDefault.instance;
        } else {
            return CloudClient.log().getLogger(name);
        }
    }

    static CloudLogger get(String name, Class<?> clz) {
        if (CloudClient.log() == null) {
            return CloudLoggerDefault.instance;
        } else {
            return CloudClient.log().getLogger(name, clz);
        }
    }

    static CloudLogger get(Class<?> clz) {
        if (CloudClient.log() == null || Utils.isEmpty(CloudProps.LOG_DEFAULT_LOGGER)) {
            return CloudLoggerDefault.instance;
        } else {
            return CloudClient.log().getLogger(CloudProps.LOG_DEFAULT_LOGGER, clz);
        }
    }


    String getName();

    void setName(String name);



    default boolean isTraceEnabled() {
        return true;
    }

    void trace(Object content);
    void trace(Meta meta, Object content);



    default boolean isDebugEnabled() {
        return true;
    }

    void debug(Object content);
    void debug(Meta meta, Object content);



    default boolean isInfoEnabled() {
        return true;
    }

    void info(Object content);

    void info(Meta meta, Object content);



    default boolean isWarnEnabled() {
        return true;
    }

    void warn(Object content);
    void warn(Meta meta, Object content);


    default boolean isErrorEnabled() {
        return true;
    }

    void error(Object content);

    void error(Meta meta, Object content);
}

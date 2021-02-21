package org.noear.solon.cloud.impl;

import org.noear.mlog.LoggerSimple;
import org.noear.solon.cloud.CloudLogger;

/**
 * @author noear 2021/2/21 created
 */
public class CloudLoggerSimple extends LoggerSimple implements CloudLogger {
    public CloudLoggerSimple(String name) {
        super(name);
    }

    public CloudLoggerSimple(Class<?> clz) {
        super(clz);
    }
}

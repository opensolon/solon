package org.noear.solon.cloud;

import org.noear.mlog.LoggerSimple;

/**
 * 云日志器
 *
 * @author noear
 * @since 1.2
 */
public class CloudLogger extends LoggerSimple {

    public CloudLogger(String name) {
        super(name);
    }

    public CloudLogger(Class<?> clz) {
        super(clz);
    }
}

package org.noear.solon.cloud;

import org.noear.mlog.Logger;

/**
 * 云日志器
 *
 * @author noear
 * @since 1.2
 */
public interface CloudLogger extends Logger {
    static CloudLogger get(String name){
        return CloudClient.log().getLogger(name);
    }

    static CloudLogger get(Class<?> clz){
        return CloudClient.log().getLogger(clz);
    }
}

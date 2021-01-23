package org.noear.solon.cloud;

import org.noear.mlog.Level;
import org.noear.mlog.Logger;
import org.noear.mlog.LoggerSimple;
import org.noear.mlog.Marker;

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

    @Override
    public void append(Level level, Marker meta, Object content) {
        super.append(level, meta, content);
    }
}

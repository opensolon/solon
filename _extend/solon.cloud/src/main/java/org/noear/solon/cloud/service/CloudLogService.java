package org.noear.solon.cloud.service;

import org.noear.mlog.Level;
import org.noear.mlog.Metainfo;

/**
 * @author noear 2021/2/23 created
 */
public interface CloudLogService {
    void append(String loggerName, Class<?> clz, Level level, Metainfo metainfo, Object content);
}

package org.noear.solon.cloud.impl;

import org.noear.logging.LogAbstractAppender;
import org.noear.mlog.Level;
import org.noear.mlog.Metainfo;
import org.noear.solon.cloud.CloudClient;

/**
 * @author noear 2021/2/23 created
 */
public class CloudLogAppender extends LogAbstractAppender {
    @Override
    public String getName() {
        return "cloud";
    }

    @Override
    protected void appendDo(String loggerName, Class<?> clz, Level level, Metainfo metainfo, Object content) {
        CloudClient.log().append(loggerName, clz, level, metainfo, content);
    }
}

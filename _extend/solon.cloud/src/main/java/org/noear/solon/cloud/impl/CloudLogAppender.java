package org.noear.solon.cloud.impl;

import org.noear.mlog.Level;
import org.noear.mlog.LogEvent;
import org.noear.solon.logging.LogAbstractAppender;
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
    public Level getDefaultLevel() {
        return Level.INFO;
    }

    @Override
    protected void appendDo(LogEvent logEvent) {
        CloudClient.log().append(logEvent);
    }
}

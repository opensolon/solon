package org.noear.solon.cloud.impl;

import org.noear.solon.cloud.CloudClient;
import org.noear.solon.logging.event.AppenderBase;
import org.noear.solon.logging.event.Level;
import org.noear.solon.logging.event.LogEvent;

/**
 * Cloud Log Appender
 *
 * @author noear
 * @since 1.6
 */
public class CloudLogAppender extends AppenderBase {
    @Override
    public Level getDefaultLevel() {
        return Level.INFO;
    }

    @Override
    public void append(LogEvent logEvent) {
        CloudClient.log().append(logEvent);
    }
}

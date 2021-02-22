package org.noear.solon.cloud.impl;

import org.noear.mlog.AppenderSimple;
import org.noear.mlog.Level;
import org.noear.mlog.LoggerFactory;
import org.noear.snack.ONode;
import org.noear.solon.Solon;
import org.noear.solon.cloud.CloudLogAppender;

/**
 * @author noear 2021/2/21 created
 */
public abstract class CloudLogAppenderSimple extends AppenderSimple implements CloudLogAppender {
    public CloudLogAppenderSimple() {
        String levelStr = Solon.cfg().get("solon.logging.appender." + getName() + ".level");
        setLevel(Level.of(levelStr, levelDefault()));

        enable = Solon.cfg().getBool("solon.logging.appender." + getName() + ".enable", true);
    }

    private boolean enable = true;
    protected boolean enable(){
        return enable;
    }

    protected Level levelDefault() {
        return LoggerFactory.getLevel();
    }

    @Override
    protected void appendContentDo(Object content) {
        if (content instanceof String) {
            System.out.println(content);
        } else {
            System.out.println(ONode.stringify(content));
        }
    }
}

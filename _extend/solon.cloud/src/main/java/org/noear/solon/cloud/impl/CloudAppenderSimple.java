package org.noear.solon.cloud.impl;

import org.noear.mlog.AppenderSimple;
import org.noear.mlog.Level;
import org.noear.mlog.LoggerFactory;
import org.noear.snack.ONode;
import org.noear.solon.Solon;

/**
 * @author noear 2021/2/21 created
 */
public abstract class CloudAppenderSimple extends AppenderSimple {
    public CloudAppenderSimple() {
        String levelStr = Solon.cfg().get("solon.logging.appender." + getName() + ".level");
        setLevel(Level.of(levelStr, levelDefault()));
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

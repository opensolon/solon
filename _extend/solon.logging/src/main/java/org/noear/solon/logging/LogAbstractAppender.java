package org.noear.solon.logging;

import org.noear.solon.Solon;
import org.noear.solon.core.util.PrintUtil;
import org.noear.solon.logging.event.AppenderSimple;
import org.noear.solon.logging.event.Level;
import org.noear.solon.logging.event.LogEvent;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author noear
 * @since 1.3
 */
public abstract class LogAbstractAppender extends AppenderSimple implements LogAppender {
    public LogAbstractAppender() {
        String levelStr = Solon.cfg().get("solon.logging.appender." + getName() + ".level");
        setLevel(Level.of(levelStr, getDefaultLevel()));

        enable = Solon.cfg().getBool("solon.logging.appender." + getName() + ".enable", true);

        Map<String, Object> meta = new LinkedHashMap();
        meta.put("level", getLevel().name());
        meta.put("enable", enable);

        PrintUtil.info("Logging", getName() + " " + meta.toString());
    }

    private boolean enable = true;

    @Override
    public boolean getEnable() {
        return enable;
    }

    private Level level;

    @Override
    public Level getLevel() {
        return level;
    }

    @Override
    public void setLevel(Level level) {
        this.level = level;
    }

    @Override
    public void append(LogEvent logEvent) {
        if (enable == false || this.level.code > logEvent.getLevel().code) {
            return;
        }

        appendDo(logEvent);
    }
}

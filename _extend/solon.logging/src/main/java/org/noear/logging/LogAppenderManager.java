package org.noear.logging;

import org.noear.mlog.Appender;
import org.noear.mlog.Level;
import org.noear.mlog.Metainfo;
import org.noear.solon.core.util.PrintUtil;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author noear
 * @since 1.3
 */
public class LogAppenderManager implements Appender {
    private static LogAppenderManager instance;

    public static LogAppenderManager getInstance() {
        if (instance == null) {
            synchronized (LogAppenderManager.class) {
                if (instance == null) {
                    instance = new LogAppenderManager();
                }
            }
        }

        return instance;
    }

    protected Map<String,Appender> appenderMap = new LinkedHashMap<>();

    public void register(LogAppender appender) {
        appenderMap.putIfAbsent(appender.getName(), appender);

        PrintUtil.green("[Logging] ");
        System.out.println("LogAppender registered from the " + appender.getClass().getTypeName() + "#" + appender.getName());
    }

    private LogAppenderManager() {
        register(new LogConsoleAppender());
    }


    @Override
    public String getName() {
        return "proxy";
    }

    @Override
    public void append(String loggerName, Class<?> clz, Level level, Metainfo metainfo, Object content) {
        for (Appender appender : appenderMap.values()) {
            appender.append(loggerName, clz, level, metainfo, content);
        }
    }
}

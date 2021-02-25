package org.noear.solon.logging;

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
public class AppenderManager implements Appender {
    private static AppenderManager instance;

    public static AppenderManager getInstance() {
        if (instance == null) {
            synchronized (AppenderManager.class) {
                if (instance == null) {
                    instance = new AppenderManager();
                }
            }
        }

        return instance;
    }

    protected Map<String,Appender> appenderMap = new LinkedHashMap<>();

    public void register(Appender appender) {
        appenderMap.putIfAbsent(appender.getName(), appender);

        PrintUtil.info("Logging","LogAppender registered from the " + appender.getClass().getTypeName() + "#" + appender.getName());
    }

    private AppenderManager() {
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

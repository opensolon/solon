package org.noear.solon.logging;

import org.noear.solon.core.util.PrintUtil;
import org.noear.solon.logging.appender.ConsoleAppender;
import org.noear.solon.logging.event.Appender;
import org.noear.solon.logging.event.LogEvent;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author noear
 * @since 1.3
 */
public class AppenderManager {
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

    protected Map<String, AppenderHolder> appenderMap = new LinkedHashMap<>();

    /**
     * 注册时，append 可能会出异常
     */
    public void register(String name, Appender appender) {
        appenderMap.putIfAbsent(name, new AppenderHolder(name, appender));

        PrintUtil.info("Logging", "LogAppender registered from the " + appender.getClass().getTypeName() + "#" + name);
    }

    private AppenderManager() {
        register("console", new ConsoleAppender());
    }

    /**
     * 添加时，register 可能会出异常
     */
    public void append(LogEvent logEvent) {
        for (AppenderHolder appender : appenderMap.values()) {
            appender.append(logEvent);
        }
    }
}

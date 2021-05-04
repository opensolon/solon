package org.noear.solon.logging;

import org.noear.solon.core.util.PrintUtil;
import org.noear.solon.logging.event.Appender;
import org.noear.solon.logging.event.LogEvent;

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

    /**
     * 注册时，append 可能会出异常
     * */
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

    /**
     * 添加时，register 可能会出异常
     * */
    @Override
    public void append(LogEvent logEvent) {
        for (Appender appender : appenderMap.values()) {
            appender.append(logEvent);
        }
    }
}

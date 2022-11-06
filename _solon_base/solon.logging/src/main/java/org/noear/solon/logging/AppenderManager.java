package org.noear.solon.logging;

import org.noear.solon.core.util.LogUtil;
import org.noear.solon.logging.appender.ConsoleAppender;
import org.noear.solon.logging.event.Appender;
import org.noear.solon.logging.event.LogEvent;

import java.util.*;

/**
 * 添加器管理员
 *
 * @author noear
 * @since 1.3
 */
public class AppenderManager {
    private static AppenderManager instance;

    /**
     * 获取单例
     */
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

    private Map<String, AppenderHolder> appenderMap = new LinkedHashMap<>();

    private AppenderManager() {
        //不能用 register，否则 LogUtil.global() 会死循环
        registerDo("console", new ConsoleAppender());
    }

    /**
     * 注册添加器
     *
     * @param name     名称
     * @param appender 添加器
     */
    public void register(String name, Appender appender) {
        registerDo(name, appender);

        LogUtil.global().warn("Logging: LogAppender registered from the " + appender.getClass().getTypeName() + "#" + name);
    }

    private void registerDo(String name, Appender appender) {
        appenderMap.putIfAbsent(name, new AppenderHolder(name, appender));
    }

    /**
     * 获取添加器
     */
    public AppenderHolder get(String name) {
        return appenderMap.get(name);
    }

    /**
     * 添加日志事件（接收日志事件的入口）
     *
     * @param logEvent 日志事件
     */
    public void append(LogEvent logEvent) {
        for (AppenderHolder appender : appenderMap.values()) {
            appender.append(logEvent);
        }
    }

    /**
     * 停止生命周期
     */
    public void stop() {
        for (AppenderHolder appender : appenderMap.values()) {
            appender.stop();
        }
    }
}

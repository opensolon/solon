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

    private static Map<String, AppenderHolder> appenderMap = new HashMap<>();

    static {
        //不能用 register，否则 LogUtil.global() 会死循环
        registerDo("console", new ConsoleAppender());
    }

    /**
     * 初始化
     * */
    public static void init(){
        //为触发 static
    }

    /**
     * 注册添加器
     *
     * @param name     名称
     * @param appender 添加器
     */
    public static void register(String name, Appender appender) {
        registerDo(name, appender);

        //打印须异步（不然可能死循环）
        LogUtil.global().infoAsync("Logging: LogAppender registered from the " + appender.getClass().getTypeName() + "#" + name);
    }

    private static void registerDo(String name, Appender appender) {
        appenderMap.putIfAbsent(name, new AppenderHolder(name, appender));
    }

    /**
     * 获取添加器
     */
    public static AppenderHolder get(String name) {
        return appenderMap.get(name);
    }

    /**
     * 添加日志事件（接收日志事件的入口）
     *
     * @param logEvent 日志事件
     */
    public static void append(LogEvent logEvent) {
        for (AppenderHolder appender : appenderMap.values()) {
            appender.append(logEvent);
        }
    }

    /**
     * 停止生命周期
     */
    public static void stop() {
        for (AppenderHolder appender : appenderMap.values()) {
            appender.stop();
        }
    }
}
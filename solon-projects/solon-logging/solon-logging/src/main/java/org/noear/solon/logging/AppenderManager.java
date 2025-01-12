/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.logging;

import org.noear.solon.logging.appender.ConsoleAppender;
import org.noear.solon.logging.event.Appender;
import org.noear.solon.logging.event.LogEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 添加器管理员
 *
 * @author noear
 * @since 1.3
 */
public class AppenderManager {

    private static Map<String,AppenderHolder> appenderMap = new ConcurrentHashMap<>();
    private static List<AppenderHolder> appenderValues = new ArrayList<>();

    static {
        //不能用 register，否则 LogUtil.global() 会死循环
        registerDo("console", new ConsoleAppender(), true);
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
        registerDo(name, appender, false);
    }

    private static void registerDo(String name, Appender appender, boolean printed) {
        appenderMap.computeIfAbsent(name, k -> {
            AppenderHolder holder = new AppenderHolder(name, appender, printed);
            appenderValues.add(holder);
            return holder;
        });
    }

    /**
     * 获取添加器
     */
    public static AppenderHolder get(String name) {
        return appenderMap.get(name);
    }

    public static int count(){
        return appenderValues.size();
    }

    /**
     * 添加日志事件（接收日志事件的入口）
     *
     * @param logEvent 日志事件
     */
    public static void append(LogEvent logEvent) {
        //用 i ，避免使用时动态添加而异常
        for (int i = 0, len = appenderValues.size(); i < len; i++) {
            AppenderHolder appender = appenderValues.get(i);
            appender.append(logEvent);
        }
    }

    /**
     * 添加日志事件（接收日志事件的入口，不支持打印的）
     *
     * @param logEvent 日志事件
     */
    public static void appendNotPrinted(LogEvent logEvent) {
        //用 i ，避免使用时动态添加而异常
        for (int i = 0, len = appenderValues.size(); i < len; i++) {
            AppenderHolder appender = appenderValues.get(i);
            if (appender.printed() == false) {
                appender.append(logEvent);
            }
        }
    }

    /**
     * 停止生命周期
     */
    public static void stop() {
        for (AppenderHolder appender : appenderValues) {
            appender.stop();
        }
    }
}
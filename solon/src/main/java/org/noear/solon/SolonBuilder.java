package org.noear.solon;

import org.noear.solon.core.NvMap;
import org.noear.solon.core.event.*;
import org.noear.solon.core.util.ConsumerEx;

/**
 * SolonApp 构建器
 *
 * @author noear
 * @since 1.4
 * @deprecated 2.5
 */
@Deprecated //用处不大，浪费代码
public class SolonBuilder {

    /**
     * 订阅事件
     *
     * @param type     事件类型
     * @param listener 监听器
     */
    public <T> SolonBuilder onEvent(Class<T> type, EventListener<T> listener) {
        EventBus.subscribe(type, listener);
        return this;
    }

    /**
     * 订阅应用初始化结束事件
     *
     * @param listener 监听器
     */
    public SolonBuilder onAppInitEnd(EventListener<AppInitEndEvent> listener) {
        return onEvent(AppInitEndEvent.class, listener);
    }

    /**
     * 订阅插件加载结束事件
     *
     * @param listener 监听器
     */
    public SolonBuilder onAppPluginLoadEnd(EventListener<AppPluginLoadEndEvent> listener) {
        return onEvent(AppPluginLoadEndEvent.class, listener);
    }

    /**
     * 订阅Bean加载结束事件
     *
     * @param listener 监听器
     */
    public SolonBuilder onAppBeanLoadEnd(EventListener<AppBeanLoadEndEvent> listener) {
        return onEvent(AppBeanLoadEndEvent.class, listener);
    }

    /**
     * 订阅应用加载结束事件
     *
     * @param listener 监听器
     */
    public SolonBuilder onAppLoadEnd(EventListener<AppLoadEndEvent> listener) {
        return onEvent(AppLoadEndEvent.class, listener);
    }

    /**
     * 订阅应用预停止事件事件
     *
     * @param listener 监听器
     */
    public SolonBuilder onAppPrestopEndEvent(EventListener<AppPrestopEndEvent> listener) {
        return onEvent(AppPrestopEndEvent.class, listener);
    }

    /**
     * 订阅应用停止事件事件
     *
     * @param listener 监听器
     */
    public SolonBuilder onAppStopEndEvent(EventListener<AppStopEndEvent> listener) {
        return onEvent(AppStopEndEvent.class, listener);
    }

    /**
     * 启动应用（全局只启动一个）
     *
     * @param source 主应用包（用于定制Bean所在包）
     * @param args   启动参数
     */
    public SolonApp start(Class<?> source, String[] args) {
        return Solon.start(source, args);
    }

    /**
     * 启动应用（全局只启动一个）
     *
     * @param source     主应用包（用于定制Bean所在包）
     * @param args       启动参数
     * @param initialize 实始化函数
     */
    public SolonApp start(Class<?> source, String[] args, ConsumerEx<SolonApp> initialize) {
        return Solon.start(source, args, initialize);
    }

    /**
     * 启动应用（全局只启动一个）
     *
     * @param source     主应用包（用于定制Bean所在包）
     * @param argx       启动参数
     * @param initialize 实始化函数
     */
    public SolonApp start(Class<?> source, NvMap argx, ConsumerEx<SolonApp> initialize) {
        return Solon.start(source, argx, initialize);
    }
}

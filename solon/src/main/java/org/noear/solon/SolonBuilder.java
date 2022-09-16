package org.noear.solon;

import org.noear.solon.annotation.Note;
import org.noear.solon.core.NvMap;
import org.noear.solon.core.event.*;
import org.noear.solon.core.util.ConsumerEx;

/**
 * SolonApp 构建器
 *
 * @author noear
 * @since 1.4
 */
public class SolonBuilder {

    /**
     * 订阅事件
     */
    public <T> SolonBuilder onEvent(Class<T> type, EventListener<T> handler) {
        EventBus.subscribe(type, handler);
        return this;
    }

    /**
     * 订阅异常事件
     */
    public SolonBuilder onError(EventListener<Throwable> handler) {
        return onEvent(Throwable.class, handler);
    }

    /**
     * 订阅应用初始化结束事件
     * */
    @Note("1.")
    public SolonBuilder onAppInitEnd(EventListener<AppInitEndEvent> handler) {
        return onEvent(AppInitEndEvent.class, handler);
    }

    /**
     * 订阅插件加载结束事件
     * */
    @Note("2.")
    public SolonBuilder onPluginLoadEnd(EventListener<PluginLoadEndEvent> handler) {
        return onEvent(PluginLoadEndEvent.class, handler);
    }

    /**
     * 订阅Bean加载结束事件
     * */
    @Note("3.")
    public SolonBuilder onBeanLoadEnd(EventListener<BeanLoadEndEvent> handler) {
        return onEvent(BeanLoadEndEvent.class, handler);
    }

    /**
     * 订阅应用加载结束事件
     * */
    @Note("4.")
    public SolonBuilder onAppLoadEnd(EventListener<AppLoadEndEvent> handler) {
        return onEvent(AppLoadEndEvent.class, handler);
    }

    /**
     * 启动应用（全局只启动一个）
     *
     * @param source 主应用包（用于定制Bean所在包）
     * @param args 启动参数
     * */
    public SolonApp start(Class<?> source, String[] args) {
        return Solon.start(source, args);
    }

    /**
     * 启动应用（全局只启动一个）
     *
     * @param source 主应用包（用于定制Bean所在包）
     * @param args 启动参数
     * @param initialize 实始化函数
     * */
    public SolonApp start(Class<?> source, String[] args, ConsumerEx<SolonApp> initialize) {
        return Solon.start(source, args, initialize);
    }

    /**
     * 启动应用（全局只启动一个）
     *
     * @param source 主应用包（用于定制Bean所在包）
     * @param argx 启动参数
     * @param initialize 实始化函数
     * */
    public SolonApp start(Class<?> source, NvMap argx, ConsumerEx<SolonApp> initialize) {
        return Solon.start(source, argx, initialize);
    }
}

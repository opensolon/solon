package org.noear.solon;

import org.noear.solon.annotation.Note;
import org.noear.solon.core.NvMap;
import org.noear.solon.core.event.*;
import org.noear.solon.ext.ConsumerEx;

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

    @Note("1.")
    public SolonBuilder onAppInitEnd(EventListener<AppInitEndEvent> handler) {
        return onEvent(AppInitEndEvent.class, handler);
    }

    @Note("2.")
    public SolonBuilder onPluginLoadEnd(EventListener<PluginLoadEndEvent> handler) {
        return onEvent(PluginLoadEndEvent.class, handler);
    }

    @Note("3.")
    public SolonBuilder onBeanLoadEnd(EventListener<BeanLoadEndEvent> handler) {
        return onEvent(BeanLoadEndEvent.class, handler);
    }

    @Note("4.")
    public SolonBuilder onAppLoadEnd(EventListener<AppLoadEndEvent> handler) {
        return onEvent(AppLoadEndEvent.class, handler);
    }

    public SolonApp start(Class<?> source, String[] args) {
        return Solon.start(source, args);
    }

    public SolonApp start(Class<?> source, String[] args, ConsumerEx<SolonApp> initialize) {
        return Solon.start(source, args, initialize);
    }

    public SolonApp start(Class<?> source, NvMap argx, ConsumerEx<SolonApp> initialize) {
        return Solon.start(source, argx, initialize);
    }
}

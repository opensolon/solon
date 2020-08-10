package org.noear.solon.core;

import java.util.HashSet;
import java.util.Set;

/**
 * 监听器（内部类，外部不要使用）
 * */
public final class XEventBus {
    /**
     * 订阅者
     */
    private static Set<HandlerHolder> subscriber = new HashSet<>();

    /**
     * 推送事件
     */
    public static void push(Object event) {
        if (event != null) {
            for (HandlerHolder h1 : subscriber) {
                if (h1.type.isInstance(event)) {
                    try {
                        h1.handler.onEvent(event);
                    } catch (Throwable ex) {}
                }
            }
        }
    }

    /**
     * 订阅事件
     */
    public static <T> void subscribe(Class<T> eventType, XEventHandler<T> handler) {
        subscriber.add(new HandlerHolder(eventType, handler));
    }

    static class HandlerHolder {
        protected Class<?> type;
        protected XEventHandler handler;

        public HandlerHolder(Class<?> type, XEventHandler handler) {
            this.type = type;
            this.handler = handler;
        }
    }
}
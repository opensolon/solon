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
    private static Set<HH> _s = new HashSet<>();

    /**
     * 推送事件
     */
    public static void push(Object event) {
        if (event != null) {
            for (HH h1 : _s) {
                if (h1.t.isInstance(event)) {
                    try {
                        h1.h.onEvent(event);
                    } catch (Throwable ex) {}
                }
            }
        }
    }

    /**
     * 订阅事件
     */
    public static <T> void subscribe(Class<T> eventType, XEventHandler<T> handler) {
        _s.add(new HH(eventType, handler));
    }

    /**
     * Handler Holder
     * */
    static class HH {
        protected Class<?> t;
        protected XEventHandler h;

        public HH(Class<?> type, XEventHandler handler) {
            this.t = type;
            this.h = handler;
        }
    }
}
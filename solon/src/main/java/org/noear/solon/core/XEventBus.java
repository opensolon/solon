package org.noear.solon.core;

import org.noear.solon.XApp;

import java.util.HashSet;
import java.util.Set;

/**
 * 监听器（内部类，外部不要使用）
 * */
public final class XEventBus {
    /**
     * 订阅者
     */
    private static Set<HH> _sThrow = new HashSet<>();
    private static Set<HH> _sOther = new HashSet<>();

    /**
     * 推送事件
     */
    public static void push(Object event) {
        if (event != null) {
            if (event instanceof Throwable) {
                //异常分发
                push0(_sThrow, event);

                if(XApp.cfg().isDebugMode()){
                    ((Throwable) event).printStackTrace();
                }
            } else {
                //其它事件分发
                push0(_sOther, event);
            }
        }
    }

    private static void push0(Set<HH> hhs, Object event) {
        for (HH h1 : hhs) {
            if (h1.t.isInstance(event)) {
                try {
                    h1.h.onEvent(event);
                } catch (Throwable ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * 订阅事件
     */
    public static <T> void subscribe(Class<T> eventType, XEventHandler<T> handler) {
        if (Throwable.class.isAssignableFrom(eventType)) {
            _sThrow.add(new HH(eventType, handler));
        } else {
            _sOther.add(new HH(eventType, handler));
        }
    }

    /**
     * Handler Holder
     */
    static class HH {
        protected Class<?> t;
        protected XEventHandler h;

        public HH(Class<?> type, XEventHandler handler) {
            this.t = type;
            this.h = handler;
        }
    }
}
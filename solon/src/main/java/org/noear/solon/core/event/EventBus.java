package org.noear.solon.core.event;

import org.noear.solon.Solon;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * 监听器（内部类，外部不要使用）
 *
 * @see org.noear.solon.core.AopContext#beanLoaded()
 * @see org.noear.solon.SolonApp#onEvent(Class, EventListener)
 * */
public final class EventBus {
    //异常订阅者
    private static Map<Object, HH> sThrow = new HashMap<>();
    //其它订阅者
    private static Map<Object, HH> sOther = new HashMap<>();

    /**
     * 异步推送事件（一般不推荐）；如果需要异常自己处理
     *
     * @param event 事件（可以是任何对象）
     */
    @Deprecated
    public static void pushAsyn(Object event) {
        pushAsync(event);
    }

    /**
     * 异步推送事件（一般不推荐）；
     *
     * @param event 事件（可以是任何对象）
     */
    public static void pushAsync(Object event) {
        if (event != null) {
            CompletableFuture.runAsync(() -> {
                push0(event);
            });
        }
    }

    /**
     * 同步推送事件
     *
     * @param event 事件（可以是任何对象）
     */
    public static void push(Object event) {
        if (event != null) {
            push0(event);
        }
    }

    private static void push0(Object event) {
        if (event instanceof Throwable) {

            if (Solon.app() == null || Solon.app().enableErrorAutoprint()) {
                ((Throwable) event).printStackTrace();
            }

            //异常分发
            push1(sThrow.values(), event, false);
        } else {
            //其它事件分发
            push1(sOther.values(), event, true);
        }
    }

    private static void push1(Collection<HH> hhs, Object event, boolean thrown) {
        for (HH h1 : hhs) {
            if (h1.t.isInstance(event)) {
                try {
                    h1.l.onEvent(event);
                } catch (Throwable e) {
                    if (thrown) {
                        EventBus.push(e);
                    } else {
                        //此处不能再转发异常
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 订阅事件
     *
     * @param eventType 事件类型
     * @param listener  事件监听者
     */
    public synchronized static <T> void subscribe(Class<T> eventType, EventListener<T> listener) {
        if (Throwable.class.isAssignableFrom(eventType)) {
            sThrow.putIfAbsent(listener, new HH(eventType, listener));

            if (Solon.app() != null) {
                Solon.app().enableErrorAutoprint(false);
            }
        } else {
            sOther.putIfAbsent(listener, new HH(eventType, listener));
        }
    }

    /**
     * 取消事件订阅
     *
     * @param listener 事件监听者
     */
    public synchronized static <T> void unsubscribe(EventListener<T> listener) {
        sThrow.remove(listener);
        sOther.remove(listener);
    }

    /**
     * Handler Holder
     */
    static class HH {
        protected Class<?> t;
        protected EventListener l;

        public HH(Class<?> type, EventListener listener) {
            this.t = type;
            this.l = listener;
        }
    }
}
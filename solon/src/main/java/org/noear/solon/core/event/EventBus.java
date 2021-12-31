package org.noear.solon.core.event;

import org.noear.solon.Solon;
import org.noear.solon.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 监听器（内部类，外部不要使用）
 *
 * @see org.noear.solon.core.AopContext#beanLoaded()
 * @see org.noear.solon.SolonApp#onEvent(Class, EventListener)
 * */
public final class EventBus {
    //异常订阅者
    private static List<HH> sThrow = new ArrayList<>();
    //其它订阅者
    private static List<HH> sOther = new ArrayList<>();

    /**
     * 异步推送事件
     *
     * @param event 事件（可以是任何对象）
     */
    @Deprecated
    public static void pushAsyn(Object event) {
        if (event != null) {
            Utils.pools.submit(() -> {
                push0(event);
            });
        }
    }

    /**
     * 推送事件
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

            if (Solon.global().enableErrorAutoprint()) {
                ((Throwable) event).printStackTrace();
            }

            //异常分发
            push1(sThrow, event, false);
        } else {
            //其它事件分发
            push1(sOther, event, true);
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
    public static <T> void subscribe(Class<T> eventType, EventListener<T> listener) {
        if (Throwable.class.isAssignableFrom(eventType)) {
            sThrow.add(new HH(eventType, listener));

            if (Solon.global() != null) {
                Solon.global().enableErrorAutoprint(false);
            }
        } else {
            sOther.add(new HH(eventType, listener));
        }
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
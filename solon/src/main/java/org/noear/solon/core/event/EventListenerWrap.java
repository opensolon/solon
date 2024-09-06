package org.noear.solon.core.event;

import org.noear.solon.core.BeanWrap;

/**
 * 事件监听包装器
 *
 * @author noear
 * @since 3.0
 */
public class EventListenerWrap implements EventListener {
    private BeanWrap w;

    public EventListenerWrap(BeanWrap w) {
        this.w = w;
    }

    @Override
    public void onEvent(Object o) throws Throwable {
        ((EventListener) w.get()).onEvent(o);
    }
}

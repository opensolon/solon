package org.noear.solon.core;

/**
 * 事件监听者
 *
 * @author noear
 * @since 1.0
 * */
public interface XEventListener<Event> {
    void onEvent(Event event);
}

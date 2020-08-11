package org.noear.solon.core;

/**
 * 事件监听者
 * */
public interface XEventListener<Event> {
    void onEvent(Event event);
}

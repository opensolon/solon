package org.noear.solon.core;

/**
 * 事件处理器
 *
 * 目前用于异常件事
 * */
public interface XEventHandler<Event> {
    void onEvent(Event event);
}

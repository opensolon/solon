package org.noear.solon.cloud.extend.cloudevent;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author 颖
 */
public interface CloudEventHandler<T> {

    /**
     * 事件处理主方法
     * @param event 要处理的事件
     */
    void handle(T event);

}

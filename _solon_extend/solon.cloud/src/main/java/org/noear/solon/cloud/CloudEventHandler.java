package org.noear.solon.cloud;

import org.noear.solon.cloud.model.Event;

/**
 * 云事件处理
 *
 * @author noear
 * @since 1.2
 */
@FunctionalInterface
public interface CloudEventHandler {
    /**
     * 处理
     *
     * @param event 事件
     * */
    boolean handle(Event event) throws Throwable;
}

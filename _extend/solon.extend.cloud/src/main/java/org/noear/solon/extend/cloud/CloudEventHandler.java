package org.noear.solon.extend.cloud;

import org.noear.solon.extend.cloud.model.Event;

/**
 * 云事件处理
 *
 * @author noear
 * @since 1.2
 */
@FunctionalInterface
public interface CloudEventHandler {
    boolean handler(Event event);
}

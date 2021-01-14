package org.noear.solon.extend.cloud;

import org.noear.solon.extend.cloud.model.Event;

/**
 * @author noear 2021/1/14 created
 */
@FunctionalInterface
public interface CloudEventHandler {
    boolean handler(Event event);
}

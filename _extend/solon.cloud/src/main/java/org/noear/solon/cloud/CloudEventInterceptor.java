package org.noear.solon.cloud;

import org.noear.solon.cloud.model.Event;

/**
 * 云事件拦截器
 *
 * @author noear
 * @since 1.6
 */
public interface CloudEventInterceptor {
    boolean doInterceptor(Event event, CloudEventHandler handler) throws Throwable;
}

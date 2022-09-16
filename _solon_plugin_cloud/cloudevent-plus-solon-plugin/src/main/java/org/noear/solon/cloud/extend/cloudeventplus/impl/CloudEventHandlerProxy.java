package org.noear.solon.cloud.extend.cloudeventplus.impl;

import org.noear.snack.ONode;
import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.extend.cloudeventplus.CloudEventEntity;
import org.noear.solon.cloud.extend.cloudeventplus.CloudEventHandlerPlus;
import org.noear.solon.cloud.model.Event;

/**
 * @author noear
 * @author 颖
 * @since 1.5
 */
public class CloudEventHandlerProxy implements CloudEventHandler {
    CloudEventHandlerPlus entityHandler;
    Class<?> entityClz;

    public CloudEventHandlerProxy(CloudEventHandlerPlus entityHandler, Class<?> entityClz) {
        this.entityHandler = entityHandler;
        this.entityClz = entityClz;
    }

    @Override
    public boolean handle(Event event) throws Throwable {
        CloudEventEntity eventEntity = ONode.deserialize(event.content(), entityClz);
        return entityHandler.handle(eventEntity);
    }
}

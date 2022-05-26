package org.noear.solon.cloud.extend.cloudeventplus.impl;

import org.noear.snack.ONode;
import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.extend.cloudeventplus.CloudEventEntity;
import org.noear.solon.cloud.model.Event;

import java.lang.reflect.Method;

/**
 * @author noear
 * @author 颖
 * @since 1.5
 */
public class CloudEventMethodProxy implements CloudEventHandler {
    Object target;
    Method method;
    Class<?> entityClz;

    public CloudEventMethodProxy(Object target, Method method, Class<?> entityClz) {
        this.target = target;
        this.method = method;
        this.entityClz = entityClz;
    }

    @Override
    public boolean handle(Event event) throws Throwable {
        CloudEventEntity eventEntity = ONode.deserialize(event.content(), entityClz);

        Object tmp = method.invoke(target, eventEntity);

        if (tmp instanceof Boolean) { //说明需要 ack
            return (boolean) tmp;
        } else {
            return true;
        }
    }
}

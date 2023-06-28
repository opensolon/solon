package org.noear.solon.cloud.eventplus.impl;

import org.noear.snack.ONode;
import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.eventplus.CloudEventEntity;
import org.noear.solon.cloud.model.Event;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.wrap.MethodWrap;

import java.lang.reflect.Method;

/**
 * @author noear
 * @author 颖
 * @since 1.5
 */
public class CloudEventMethodProxy implements CloudEventHandler {
    BeanWrap target;
    MethodWrap method;
    Class<?> entityClz;

    public CloudEventMethodProxy(BeanWrap target, Method method, Class<?> entityClz) {
        this.target = target;
        this.method = target.context().methodGet(method);
        this.entityClz = entityClz;
    }

    @Override
    public boolean handle(Event event) throws Throwable {
        CloudEventEntity eventEntity = ONode.deserialize(event.content(), entityClz);

        Object tmp = method.invokeByAspect(target.get(), new Object[]{eventEntity});

        if (tmp instanceof Boolean) { //说明需要 ack
            return (boolean) tmp;
        } else {
            return true;
        }
    }
}

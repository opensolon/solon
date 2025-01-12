/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.cloud.eventplus.impl;

import org.noear.snack.ONode;
import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.eventplus.CloudEventEntity;
import org.noear.solon.cloud.model.Event;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.wrap.MethodWrap;

import java.lang.reflect.Method;

/**
 * 云事件处理方法原型代理（也支持拦截）
 *
 * @author noear
 * @author 颖
 * @since 1.5
 */
public class CloudEventMethodProxy implements CloudEventHandler {
    private BeanWrap target;
    private MethodWrap method;
    private Class<?> entityClz;

    public CloudEventMethodProxy(BeanWrap target, Method method, Class<?> entityClz) {
        this.target = target;
        this.method = target.context().methodGet(target.rawClz(), method);
        this.entityClz = entityClz;
    }

    public BeanWrap getTarget() {
        return target;
    }

    public MethodWrap getMethod() {
        return method;
    }

    @Override
    public boolean handle(Event event) throws Throwable {
        CloudEventEntity eventEntity = ONode.deserialize(event.content(), entityClz);

        Object tmp = method.invokeByAspect(target.get(true), new Object[]{eventEntity});

        if (tmp instanceof Boolean) { //说明需要 ack
            return (boolean) tmp;
        } else {
            return true;
        }
    }
}

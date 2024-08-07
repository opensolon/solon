/*
 * Copyright 2017-2024 noear.org and authors
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
import org.noear.solon.cloud.eventplus.CloudEventHandlerPlus;
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

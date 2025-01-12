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

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.annotation.CloudEvent;
import org.noear.solon.cloud.eventplus.CloudEventSubscribe;
import org.noear.solon.core.BeanExtractor;
import org.noear.solon.core.BeanWrap;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @author noear
 * @author 颖
 * @since 1.5
 */
public class CloudEventSubscribeBeanExtractor implements BeanExtractor<CloudEventSubscribe> {

    @Override
    public void doExtract(BeanWrap bw, Method method, CloudEventSubscribe anno) {
        if (CloudClient.event() == null) {
            throw new IllegalArgumentException("Missing CloudEventService component");
        }

        Parameter[] args = method.getParameters();
        if (args.length != 1) {
            throw new IllegalArgumentException("Missing CloudEventSubscriber method need one parameter");
        }

        Class<?> entityClz = args[0].getType();

        CloudEvent anno2 = entityClz.getAnnotation(CloudEvent.class);

        if (anno2 == null) {
            throw new IllegalArgumentException("The entity is missing (@CloudEvent) annotations: " + this.getClass().getName());
        }

        //支持${xxx}配置
        String topic2 = Solon.cfg().getByTmpl(Utils.annoAlias(anno2.value(), anno2.topic()));
        //支持${xxx}配置
        String group2 = Solon.cfg().getByTmpl(anno2.group());

        CloudEventMethodProxy hadnler2 = new CloudEventMethodProxy(bw, method, entityClz);

        CloudManager.register(anno2, hadnler2);

        if (CloudClient.enableEvent()) {
            CloudClient.event().attention(anno2.level(), anno2.channel(), group2, topic2, anno2.tag(), anno2.qos(), hadnler2);
        }
    }
}

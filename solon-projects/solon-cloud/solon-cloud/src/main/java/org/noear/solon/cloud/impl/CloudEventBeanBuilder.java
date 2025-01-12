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
package org.noear.solon.cloud.impl;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.annotation.CloudEvent;
import org.noear.solon.cloud.proxy.CloudEventHandlerProxy;
import org.noear.solon.core.BeanBuilder;
import org.noear.solon.core.BeanWrap;

/**
 * @author noear
 * @since 1.4
 */
public class CloudEventBeanBuilder implements BeanBuilder<CloudEvent> {
    @Override
    public void doBuild(Class<?> clz, BeanWrap bw, CloudEvent anno) throws Exception {
        if (CloudClient.event() == null) {
            throw new IllegalArgumentException("Missing CloudEventService component");
        }

        if (bw.raw() instanceof CloudEventHandler) {
            CloudEventHandler handler = new CloudEventHandlerProxy(bw);

            CloudManager.register(anno, handler);//原型代理

            //支持${xxx}配置
            String topic = Solon.cfg().getByTmpl(Utils.annoAlias(anno.value(), anno.topic()));
            //支持${xxx}配置
            String group = Solon.cfg().getByTmpl(anno.group());

            //关注事件
            CloudClient.event().attention(anno.level(), anno.channel(), group, topic, anno.tag(), anno.qos(), handler);
        }
    }
}
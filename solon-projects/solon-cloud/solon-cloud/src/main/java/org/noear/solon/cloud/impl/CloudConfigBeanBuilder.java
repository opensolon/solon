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
import org.noear.solon.cloud.CloudConfigHandler;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.annotation.CloudConfig;
import org.noear.solon.cloud.model.Config;
import org.noear.solon.cloud.proxy.CloudConfigHandlerProxy;
import org.noear.solon.core.BeanBuilder;
import org.noear.solon.core.BeanWrap;

/**
 * @author noear
 * @since 1.4
 */
public class CloudConfigBeanBuilder implements BeanBuilder<CloudConfig> {
    @Override
    public void doBuild(Class<?> clz, BeanWrap bw, CloudConfig anno) throws Exception {
        if (CloudClient.config() == null) {
            throw new IllegalArgumentException("Missing CloudConfigService component");
        }

        if (bw.raw() instanceof CloudConfigHandler) {
            CloudConfigHandler handler = new CloudConfigHandlerProxy(bw); //原型代理

            CloudManager.register(anno, handler);

            //支持${xxx}配置
            String name = Solon.cfg().getByTmpl(Utils.annoAlias(anno.value(), anno.name()));
            //支持${xxx}配置
            String group = Solon.cfg().getByTmpl(anno.group());

            Config config = CloudClient.config().pull(group, name);
            if (config != null) {
                handler.handle(config);
            }

            //关注配置
            CloudClient.config().attention(group, name, handler);
        }
    }
}

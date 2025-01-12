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
import org.noear.solon.cloud.CloudJobHandler;
import org.noear.solon.cloud.annotation.CloudJob;
import org.noear.solon.cloud.proxy.CloudJobHandlerBeanProxy;
import org.noear.solon.core.BeanBuilder;
import org.noear.solon.core.BeanWrap;

/**
 * @author noear
 * @since 1.4
 * @since 2.9
 */
public class CloudJobBeanBuilder implements BeanBuilder<CloudJob> {
    /**
     * @since 2.9
     */
    @Override
    public void doBuild(Class<?> clz, BeanWrap bw, CloudJob anno) throws Throwable {
        if (CloudClient.job() == null) {
            throw new IllegalArgumentException("Missing CloudJobService component");
        }

        if (bw.raw() instanceof CloudJobHandler) {
            CloudJobHandler handler = new CloudJobHandlerBeanProxy(bw);

            //支持${xxx}配置
            String name = Solon.cfg().getByTmpl(Utils.annoAlias(anno.value(), anno.name()));
            //支持${xxx}配置
            String description = Solon.cfg().getByTmpl(anno.description());

            CloudClient.job().register(name, anno.cron7x(), description, handler);
        }
    }
}
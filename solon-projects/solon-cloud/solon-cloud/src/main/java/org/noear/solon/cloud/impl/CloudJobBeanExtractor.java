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
import org.noear.solon.cloud.proxy.CloudJobHandlerMethodProxy;
import org.noear.solon.core.BeanExtractor;
import org.noear.solon.core.BeanWrap;

import java.lang.reflect.Method;

/**
 * @author noear
 * @since 1.4
 */
public class CloudJobBeanExtractor implements BeanExtractor<CloudJob> {
    @Override
    public void doExtract(BeanWrap bw, Method method, CloudJob anno) {
        if (CloudClient.job() == null) {
            throw new IllegalArgumentException("Missing CloudJobService component");
        }

        //支持${xxx}配置
        String name = Solon.cfg().getByTmpl(anno.value());
        if (Utils.isEmpty(name)) {
            name = Solon.cfg().getByTmpl(anno.name());
        }
        //支持${xxx}配置
        String description = Solon.cfg().getByTmpl(anno.description());

        if (name.trim().length() == 0) {
            throw new IllegalStateException("CloudJob name invalid, for[" + bw.clz() + "#" + method.getName() + "] .");
        }
        if (CloudClient.job().isRegistered(name)) {
            throw new IllegalStateException("CloudJob[" + name + "] naming conflicts.");
        }

        CloudJobHandler handler = new CloudJobHandlerMethodProxy(bw, method);

                //method 可以有返回结果
        method.setAccessible(true);

        CloudClient.job().register(name, anno.cron7x(), description, handler);
    }
}

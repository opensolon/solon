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
package org.noear.solon.cloud.model;

import org.noear.solon.cloud.CloudJobHandler;
import org.noear.solon.cloud.CloudJobHandlerChain;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.core.handle.Context;

/**
 * 任务处理实体
 *
 * @author noear
 * @since 1.4
 */
public class JobHolder implements CloudJobHandler {
    private final String name;
    private final String cron7x;
    private final String description;
    private final CloudJobHandler handler;

    public JobHolder(String name, String cron7x, String description, CloudJobHandler handler) {
        this.name = name;
        this.cron7x = cron7x;
        this.description = description;
        this.handler = handler;
    }

    /**
     * 获取任务
     */
    public String getName() {
        return name;
    }

    /**
     * 获取计划表达式
     */
    public String getCron7x() {
        return cron7x;
    }

    /**
     * 获取描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * ::起到代理作用，从而附加能力
     *
     * @since 1.6
     */
    @Override
    public void handle(Context ctx) throws Throwable {
        if (CloudManager.jobInterceptors() == null || CloudManager.jobInterceptors().isEmpty()) {
            handler.handle(ctx);
        } else {
            Job job = new JobImpl(this, ctx);
            CloudJobHandlerChain handlerChain = new CloudJobHandlerChain(job, handler, CloudManager.jobInterceptors());
            handlerChain.handle(ctx);
        }
    }
}

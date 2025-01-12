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
package org.noear.solon.cloud;

import org.noear.solon.cloud.model.Job;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.util.RankEntity;

import java.util.List;

/**
 * 云任务处理处理链
 *
 * @author noear
 * @since 2.7
 */
public class CloudJobHandlerChain implements CloudJobHandler{
    private Job job;
    private CloudJobHandler handler;
    private List<RankEntity<CloudJobInterceptor>> interceptors;
    private int indexVal;

    public CloudJobHandlerChain(Job job, CloudJobHandler handler, List<RankEntity<CloudJobInterceptor>> interceptors) {
        this.job = job;
        this.handler = handler;
        this.interceptors = interceptors;
        this.indexVal = 0;
    }

    @Override
    public void handle(Context ctx) throws Throwable {
        if (interceptors.size() > indexVal) {
            interceptors.get(indexVal++).target.doIntercept(job, this);
        } else {
            handler.handle(ctx);
        }
    }
}

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
import org.noear.solon.cloud.CloudJobHandler;
import org.noear.solon.cloud.CloudJobInterceptor;
import org.noear.solon.cloud.service.CloudJobService;
import org.noear.solon.core.util.RankEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author noear
 * @since 1.6
 */
public class CloudJobServiceManagerImpl implements CloudJobServiceManager {
    private final CloudJobService service;
    private List<RankEntity<CloudJobInterceptor>> jobInterceptors = new ArrayList<>();

    public CloudJobServiceManagerImpl(CloudJobService service) {
        this.service = service;
        Solon.context().subWrapsOfType(CloudJobInterceptor.class, bw -> {
            addJobInterceptor(bw.index(), bw.raw());
        });
    }

    /**
     * 添加任务拦截器
     *
     * @since 2.7
     */
    @Override
    public void addJobInterceptor(int index, CloudJobInterceptor jobInterceptor) {
        jobInterceptors.add(new RankEntity<>(jobInterceptor, index));
    }

    /**
     * 获取任务拦截器
     */
    @Override
    public List<RankEntity<CloudJobInterceptor>> getJobInterceptors() {
        return Collections.unmodifiableList(jobInterceptors);
    }

    /**
     * 注册任务
     */
    @Override
    public boolean register(String name, String cron7x, String description, CloudJobHandler handler) {
        return service.register(name, cron7x, description, handler);
    }

    /**
     * 是否已注册
     */
    @Override
    public boolean isRegistered(String name) {
        return service.isRegistered(name);
    }
}

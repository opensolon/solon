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

import org.noear.solon.cloud.CloudEventInterceptor;
import org.noear.solon.cloud.service.CloudEventService;
import org.noear.solon.cloud.service.CloudEventServicePlus;


/**
 * 事件服务管理器及代理（用以支持多通道）
 *
 * @author noear
 * @since 1.3
 */
public interface CloudEventServiceManager extends CloudEventService {

    /**
     * 获取事件拦截器
     */
    CloudEventInterceptor getEventInterceptor();

    /**
     * 注册事件服务
     *
     * @param service 事件服务
     */
    void register(CloudEventServicePlus service);

    /**
     * 获取事件服务
     *
     * @param channel 通道
     */
    CloudEventServicePlus get(String channel);

    /**
     * 获取事件服务，如果没有则异常
     *
     * @param channel 通道
     */
    CloudEventServicePlus getOrThrow(String channel);
}

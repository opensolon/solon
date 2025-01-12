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
package org.noear.solon.cloud.service;

import org.noear.solon.cloud.CloudJobHandler;

/**
 * 云端定时任务服务
 *
 * @author noear
 * @since 1.3
 */
public interface CloudJobService {
    /**
     * 注册任务
     *
     * @param name 任务名
     * @param cron7x 计划表达式
     * @param description 描述
     * @param handler 处理器
     */
    boolean register(String name, String cron7x, String description, CloudJobHandler handler);

    /**
     * 是否已注册
     *
     * @param name 任务名
     */
    boolean isRegistered(String name);
}

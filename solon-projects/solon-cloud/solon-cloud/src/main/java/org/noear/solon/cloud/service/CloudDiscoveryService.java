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

import org.noear.solon.cloud.CloudDiscoveryHandler;
import org.noear.solon.cloud.model.Discovery;
import org.noear.solon.cloud.model.Instance;

import java.util.Collection;

/**
 * 云端注册与发现服务
 *
 * @author noear
 * @since 1.2
 */
public interface CloudDiscoveryService {
    /**
     * 注册服务实例
     *
     * @param group    分组
     * @param instance 服务实例
     */
    void register(String group, Instance instance);

    /**
     * 注册服务实例
     *
     * @param group    分组
     * @param instance 服务实例
     * @param health   是否健康
     */
    void registerState(String group, Instance instance, boolean health);


    /**
     * 注销服务实例
     *
     * @param group    分组
     * @param instance 服务实例
     */
    void deregister(String group, Instance instance);

    /**
     * 查询服务实例列表
     *
     * @param group   分组
     * @param service 服各名
     */
    Discovery find(String group, String service);

    /**
     * 查询服务列表
     *
     * @param group 分组
     */
    Collection<String> findServices(String group);

    /**
     * 关注服务实例列表
     *
     * @param group    分组
     * @param service  服各名
     * @param observer 观察者
     */
    void attention(String group, String service, CloudDiscoveryHandler observer);
}
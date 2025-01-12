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

import org.noear.solon.Solon;
import org.noear.solon.cloud.CloudConfigHandler;
import org.noear.solon.cloud.model.Config;

/**
 * 云端配置服务
 *
 * @author noear
 * @since 1.2
 */
public interface CloudConfigService {
    /**
     * 拉取配置
     *
     * @param group 分组
     * @param name 配置名
     * @return 配置
     */
    Config pull(String group, String name);

    /**
     * 拉取配置
     *
     * @param name 配置名
     * @return 配置
     */
    default Config pull(String name){
        return pull(Solon.cfg().appGroup(), name);
    }

    /**
     * 推送配置
     *
     * @param group 分组
     * @param name 配置名
     * @param value 值
     * @return 是否成功
     */
    boolean push(String group, String name, String value);

    /**
     * 推送配置
     *
     * @param name 配置名
     * @param value 值
     * @return 是否成功
     */
    default boolean push(String name, String value) {
        return push(Solon.cfg().appGroup(), name, value);
    }


    /**
     * 移除配置
     *
     * @param group 分组
     * @param name 配置名
     * @return 是否成功
     */
    boolean remove(String group, String name);

    /**
     * 移除配置
     *
     * @param name 配置名
     * @return 是否成功
     */
    default boolean remove(String name){
        return remove(Solon.cfg().appGroup(), name);
    }

    /**
     * 关注配置
     *
     * @param group 分组
     * @param name 配置名
     * @param observer 观察者
     */
    void attention(String group, String name, CloudConfigHandler observer);
}

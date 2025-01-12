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


/**
 * 云端事件加强服务（事件总线服务）
 *
 * @author noear
 * @since 1.5
 */
public interface CloudEventServicePlus extends CloudEventService{
    /**
     * 获取通道配置
     * */
    String getChannel();

    /**
     * 获取默认分组配置（即给所有的发送和订阅加上分组）
     * */
    String getGroup();
}

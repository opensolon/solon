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
package org.noear.solon.logging.persistent;

import java.util.Collection;

/**
 * 打包队列（一个个添加；打包后批量处理）
 *
 * @author noear
 * @since 2.2
 */
public interface PackagingQueueTask<Event> {
    /**
     * 设置工作处理
     * */
    void setWorkHandler(PackagingWorkHandler<Event> workHandler);
    /**
     * 设置空闲休息时间
     * */
    void setIdleInterval(long idleInterval);
    /**
     * 设置包装合大小
     * */
    void setPacketSize(int packetSize);

    /**
     * 添加
     * */
    void add(Event event);
    /**
     * 添加一批
     * */
    void addAll(Collection<Event> events);
}

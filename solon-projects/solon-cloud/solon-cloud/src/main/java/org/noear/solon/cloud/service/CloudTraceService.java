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
 * 云端链路跟踪服务
 *
 * @author noear
 * @since 1.2
 */
public interface CloudTraceService {
    /**
     * TraceId 头名称
     */
    String HEADER_TRACE_ID_NAME();

    /**
     * FromId 头名称
     * */
    String HEADER_FROM_ID_NAME();

    /**
     * 设置当前线程的跟踪标识
     * */
    void setLocalTraceId(String traceId);

    /**
     * 获取跟踪标识
     */
    String getTraceId();

    /**
     * 获取来源标识（service@address:port）
     * */
    String getFromId();

}

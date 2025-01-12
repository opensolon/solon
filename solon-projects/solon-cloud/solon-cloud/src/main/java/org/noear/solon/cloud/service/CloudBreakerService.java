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

import org.noear.solon.cloud.model.BreakerException;

/**
 * 云端断路器服务
 *
 * @author noear
 * @since 1.3
 */
public interface CloudBreakerService {
    /**
     * 获取入口
     *
     * @param breakerName 断路器名称
     */
    AutoCloseable entry(String breakerName) throws BreakerException;
}

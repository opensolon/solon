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
package org.noear.solon.health;

import java.io.Serializable;

/**
 * 健康检查结果
 *
 * @author iYarnFog
 * @since 1.5
 */
public class HealthCheckResult implements Serializable {
    private HealthStatus status = HealthStatus.UP;
    private Object details;

    /**
     * 获取状态
     * */
    public HealthStatus getStatus() {
        return status;
    }

    /**
     * 设置状态
     * */
    public void setStatus(HealthStatus status) {
        this.status = status;
    }

    /**
     * 设置详情
     * */
    public void setDetails(Object details) {
        this.details = details;
    }
}

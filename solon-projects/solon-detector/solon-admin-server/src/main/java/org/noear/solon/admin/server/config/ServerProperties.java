/*
 * Copyright 2017-2024 noear.org and authors
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
package org.noear.solon.admin.server.config;

import lombok.Data;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

import java.util.Map;

/**
 * 配置文件
 *
 * @author shaokeyibb
 * @since 2.3
 */
@Inject(value = "${solon.admin.server}", required = false)
@Configuration
@Data
public class ServerProperties {
    /**
     * 是否启用
     * */
    private boolean enabled = true;
    /**
     * 模式
     * */
    private String mode = "local";

    private long heartbeatInterval = 10 * 1000;

    private long clientMonitorPeriod = 2 * 1000;

    private long connectTimeout = 5 * 1000;

    private long readTimeout = 5 * 1000;

    /**
     * 介绍路径
     * */
    private String uiPath = "/";
    /**
     * base 签权
     * */
    private Map<String, String> basicAuth;
}

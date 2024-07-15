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
package org.noear.solon.admin.client.config;

import lombok.Data;

/**
 * 本地模式配置文件
 *
 * @author shaokeyibb
 * @since 2.3
 */
@Data
public class ClientProperties {

    private boolean enabled = true;

    private String mode = "local";

    private String token = "3C41D632-A070-060C-40D2-6D84B3C07094";

    private String serverUrl = "http://localhost:8080";

    private long heartbeatInterval = 10 * 1000;

    private long connectTimeout = 5 * 1000;

    private long readTimeout = 5 * 1000;

    private boolean showSecretInformation = false;
}

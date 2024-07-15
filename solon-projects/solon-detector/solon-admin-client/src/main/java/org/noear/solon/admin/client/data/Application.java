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
package org.noear.solon.admin.client.data;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 应用程序数据
 *
 * @author shaokeyibb
 * @since 2.3
 */
@Data
@Builder
public class Application {

    private final String name;

    private final String token;

    private String baseUrl;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private final String metadata;

    /**
     * 是否展示敏感信息，如：环境变量
     */
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private final boolean showSecretInformation;

    /**
     * 环境信息
     */
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private final EnvironmentInformation environmentInformation;

}

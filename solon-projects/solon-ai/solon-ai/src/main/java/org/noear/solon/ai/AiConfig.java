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
package org.noear.solon.ai;

import org.noear.solon.lang.Preview;

import java.util.Properties;

/**
 * AI 配置
 *
 * @author noear
 * @since 3.1
 */
@Preview("3.1")
public interface AiConfig {
    /**
     * 请求地址
     */
    String url();

    /**
     * 提供者
     */
    String provider();

    /**
     * 模型
     */
    String model();

    /**
     * 转为属性
     */
    Properties toProps();
}

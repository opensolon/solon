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
package org.noear.solon.ai.chat;

import org.noear.solon.ai.AiConfig;
import org.noear.solon.core.util.MultiMap;

import java.util.*;

/**
 * 聊天配置
 *
 * @author noear
 * @since 3.1
 */
public class ChatConfig implements AiConfig {
    protected String apiUrl;
    protected String apiKey;
    protected String provider;
    protected String model;
    protected ChatDialect dialect;
    protected final MultiMap<String> headers = new MultiMap<>();
    protected final Map<String, ChatFunction> globalFunctions = new LinkedHashMap<>();


    @Override
    public String apiUrl() {
        return apiUrl;
    }

    @Override
    public String apiKey() {
        return apiKey;
    }

    @Override
    public String provider() {
        return provider;
    }

    public ChatDialect dialect() {
        return dialect;
    }

    @Override
    public String model() {
        return model;
    }

    @Override
    public MultiMap<String> headers() {
        return headers;
    }

    public Collection<ChatFunction> globalFunctions() {
        return globalFunctions.values();
    }

    public ChatFunction globalFunction(String name) {
        return globalFunctions.get(name);
    }
}

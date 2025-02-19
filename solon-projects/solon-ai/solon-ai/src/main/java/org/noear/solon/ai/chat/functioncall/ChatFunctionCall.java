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
package org.noear.solon.ai.chat.functioncall;

import java.io.Serializable;
import java.util.Map;

/**
 * 聊天函数调用
 *
 * @author noear
 * @since 3.1
 */
public class ChatFunctionCall implements Serializable {
    private String id;
    private String name;
    private Map<String, Object> arguments;

    public ChatFunctionCall(String id, String name, Map<String, Object> arguments) {
        this.id = id;
        this.name = name;
        this.arguments = arguments;
    }

    /**
     * 调用id（用于回传）
     */
    public String id() {
        return id;
    }

    /**
     * 函数名字
     */
    public String name() {
        return name;
    }

    /**
     * 调用参数
     */
    public Map<String, Object> arguments() {
        return arguments;
    }
}

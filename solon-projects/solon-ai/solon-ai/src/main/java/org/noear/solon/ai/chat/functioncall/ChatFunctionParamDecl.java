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

/**
 * @author noear
 * @since 3.1
 */
public class ChatFunctionParamDecl implements ChatFunctionParam {
    private final String name;
    private final Class<?> type;
    private final String description;

    public ChatFunctionParamDecl(String name, Class<?> type, String description) {
        this.name = name;
        this.type = type;
        this.description = description;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Class<?> type() {
        return type;
    }

    @Override
    public String description() {
        return description;
    }
}

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
package org.noear.solon.aot;

import org.noear.solon.core.AppContext;

/**
 * aot 阶段，注册 native 运行时的元信息，其实现类需要是一个 solon bean
 *
 * @author songyinyin
 * @since 2023/4/13 19:02
 */
public interface RuntimeNativeRegistrar {

    /**
     * aot 阶段，注册 native 运行时元信息
     *
     * @param context  上下文
     * @param metadata 原生运行时元信息
     */
    void register(AppContext context, RuntimeNativeMetadata metadata);
}

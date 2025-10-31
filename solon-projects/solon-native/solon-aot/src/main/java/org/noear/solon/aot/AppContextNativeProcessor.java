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
 * Aot 阶段，对 {@link org.noear.solon.core.AppContext} 中的类、方法、字段、实体、代理等...添加原生元数据
 *
 * @author songyinyin
 * @since 2.2
 */
public interface AppContextNativeProcessor {


    /**
     * 处理（生成配置、代理等...）
     *
     * @param context  上下文
     * @param settings 运行设置
     * @param metadata 元信息对象
     */
    void process(AppContext context, Settings settings, RuntimeNativeMetadata metadata) throws Throwable;
}

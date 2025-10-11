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
package org.noear.solon.core.handle;

/**
 * 渲染器工厂
 *
 * @author noear
 * @since 2.7
 * @deprecated 3.6
 */
@Deprecated
public interface RenderFactory {
    /**
     * 映射
     *
     * @since 3.0
     */
    String[] mappings();

    /**
     * 创建渲染器
     */
    Render create();
}

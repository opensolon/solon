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
 * 通用渲染接口
 *
 * @see RenderManager#register(Render)
 * @author noear
 * @since 1.0
 * */
@FunctionalInterface
public interface Render {
    /**
     * 名字
     */
    default String name() {
        return this.getClass().getSimpleName();
    }

    /**
     * 是否匹配
     *
     * @param ctx  上下文
     * @param mime 媒体类型
     */
    default boolean matched(Context ctx, String mime) {
        return false;
    }

    /**
     * 渲染并返回（默认不实现）
     */
    default String renderAndReturn(Object data, Context ctx) throws Throwable {
        return null;
    }

    /**
     * 渲染
     *
     * @param data 数据
     * @param ctx  上下文
     */
    void render(Object data, Context ctx) throws Throwable;

}

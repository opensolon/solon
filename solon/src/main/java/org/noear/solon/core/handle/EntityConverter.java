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

import org.noear.solon.core.wrap.MethodWrap;
import org.noear.solon.lang.Preview;

/**
 * 实体转换器（预览，未启用）
 *
 * @author noear
 * @since 3.2
 */
@Preview("3.2")
public interface EntityConverter {
    /**
     * 实例检测（移除时用）
     */
    default boolean isInstance(Class<?> clz) {
        return clz.isInstance(this);
    }

    /**
     * 名字
     */
    default String name() {
        return this.getClass().getSimpleName();
    }

    /**
     * 映射
     */
    default String[] mappings() {
        return null;
    }

    /**
     * 是否允许写
     *
     */
    boolean allowWrite();

    /**
     * 是否能写
     */
    boolean canWrite(String mime, Context ctx);

    /**
     * 写入并返回（渲染并返回（默认不实现））
     */
    String writeAndReturn(Object data, Context ctx) throws Throwable;

    /**
     * 写入
     *
     * @param data 数据
     * @param ctx  上下文
     */
    void write(Object data, Context ctx) throws Throwable;

    /**
     * 是否允许读
     */
    boolean allowRead();

    /**
     * 是否能读
     */
    boolean canRead(Context ctx, String mime);

    /**
     * 读取（参数分析）
     *
     * @param ctx    上下文
     * @param target 控制器
     * @param mWrap  函数包装器
     */
    Object[] read(Context ctx, Object target, MethodWrap mWrap) throws Throwable;
}
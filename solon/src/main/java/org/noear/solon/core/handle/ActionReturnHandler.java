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
 * 动作返回处理器
 *
 * @author noear
 * @since 2.3
 */
public interface ActionReturnHandler {
    /**
     * 是否匹配
     *
     * @param ctx        上下文
     * @param returnType 返回类型
     */
    boolean matched(Context ctx, Class<?> returnType);

    /**
     * 返回处理
     *
     * @param ctx         上下文
     * @param action      动作
     * @param returnValue 返回值
     */
    void returnHandle(Context ctx, Action action, Object returnValue) throws Throwable;
}
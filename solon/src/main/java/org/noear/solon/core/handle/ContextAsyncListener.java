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

import java.io.IOException;

/**
 * 通用上下文接口 - 异步监听
 *
 * @author noear
 * @since 2.3
 */
public interface ContextAsyncListener {
    /**
     * 开始
     *
     * @param ctx 请求上下文
     */
    void onStart(Context ctx) throws IOException;

    /**
     * 完成
     *
     * @param ctx 请求上下文
     */
    void onComplete(Context ctx) throws IOException;

    /**
     * 超时
     *
     * @param ctx 请求上下文
     */
    void onTimeout(Context ctx) throws IOException;

    /**
     * 出错
     *
     * @param ctx 请求上下文
     * @param e   异步
     */
    void onError(Context ctx, Throwable e) throws IOException;
}

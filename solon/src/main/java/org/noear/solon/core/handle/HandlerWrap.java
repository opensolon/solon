/*
 * Copyright 2017-2024 noear.org and authors
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

import org.noear.solon.core.BeanWrap;

/**
 * 通用处理接口包装（支持非单例）
 *
 * @author noear
 * @since 2.9
 */
public class HandlerWrap implements Handler {
    private BeanWrap bw;

    public HandlerWrap(BeanWrap bw) {
        this.bw = bw;
    }

    @Override
    public void handle(Context ctx) throws Throwable {
        ((Handler) bw.get()).handle(ctx);
    }
}

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
package org.noear.solon.rx.handle;

import org.noear.solon.core.handle.Context;

/**
 * 响应式上下文默认实现
 *
 * @author noear
 * @since 3.1
 */
public class RxContextDefault implements RxContext {
    private final Context real;

    public RxContextDefault(Context ctx) {
        if (ctx.asyncSupported() == false) {
            throw new IllegalStateException("This ctx does not support asynchronous mode");
        } else {
            if (ctx.asyncStarted() == false) {
                ctx.asyncStart(-1L, null);
            }
        }

        this.real = ctx;
    }

    @Override
    public <T> T attr(String key) {
        return real.attr(key);
    }

    @Override
    public void attrSet(String key, Object value) {
        real.attrSet(key, value);
    }

    @Override
    public String realIp() {
        return real.realIp();
    }

    @Override
    public boolean isSecure() {
        return real.isSecure();
    }

    /**
     * 转为经典上下文接口
     */
    @Override
    public Context toContext() {
        return real;
    }
}
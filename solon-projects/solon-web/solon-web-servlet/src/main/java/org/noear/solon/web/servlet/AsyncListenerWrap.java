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
package org.noear.solon.web.servlet;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ContextAsyncListener;

import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import java.io.IOException;

/**
 * @author noear
 * @since 2.3
 */
public class AsyncListenerWrap implements AsyncListener {
    final Context ctx;
    final ContextAsyncListener real;

    public AsyncListenerWrap(Context ctx, ContextAsyncListener real) {
        this.ctx = ctx;
        this.real = real;
    }

    @Override
    public void onComplete(AsyncEvent asyncEvent) throws IOException {
        real.onComplete(ctx);
    }

    @Override
    public void onTimeout(AsyncEvent asyncEvent) throws IOException {
        real.onTimeout(ctx);
    }

    @Override
    public void onError(AsyncEvent asyncEvent) throws IOException {
        real.onError(ctx, asyncEvent.getThrowable());
    }

    @Override
    public void onStartAsync(AsyncEvent asyncEvent) throws IOException {
        real.onStart(ctx);
    }
}

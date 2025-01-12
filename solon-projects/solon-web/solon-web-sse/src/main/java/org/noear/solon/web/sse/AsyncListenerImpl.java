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
package org.noear.solon.web.sse;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ContextAsyncListener;

import java.io.IOException;

/**
 * @author noear
 * @since 2.3
 */
public class AsyncListenerImpl implements ContextAsyncListener {
    SseEmitterHandler handler;

    public AsyncListenerImpl(SseEmitterHandler handler) {
        this.handler = handler;
    }

    @Override
    public void onStart(Context ctx)  {

    }

    @Override
    public void onComplete(Context ctx)  throws IOException{
        handler.stop();
    }

    @Override
    public void onTimeout(Context ctx)  throws IOException{
        handler.stopOnTimeout();
    }

    @Override
    public void onError(Context ctx, Throwable e) throws IOException{
        handler.stopOnError(e);
    }
}

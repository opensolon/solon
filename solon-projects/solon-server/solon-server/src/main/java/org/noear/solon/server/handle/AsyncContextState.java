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
package org.noear.solon.server.handle;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ContextAsyncListener;
import org.noear.solon.core.util.RunUtil;
import org.noear.solon.core.util.RunnableEx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;

/**
 * 异步上下文状态
 *
 * @author noear
 * @since 3.0
 */
public class AsyncContextState implements ContextAsyncListener {
    static final Logger log = LoggerFactory.getLogger(AsyncContextState.class);
    static final long timeoutDef = 30000L; //默认30秒

    public boolean isStarted = false;
    public CompletableFuture<Object> asyncFuture;

    private ScheduledFuture<?> timeoutFuture;
    private List<ContextAsyncListener> listeners;

    public void addListener(ContextAsyncListener listener) {
        if (listeners == null) {
            listeners = new CopyOnWriteArrayList<>();
        }

        if (listener != null) {
            listeners.add(listener);
        }
    }

    public void asyncDelay(long timeout, Context ctx, RunnableEx onTimeout) {
        if (timeout == 0) {
            timeout = timeoutDef;
        }

        if (timeout > 0) {
            timeoutFuture = RunUtil.delay(() -> {
                try {
                    //超时异常
                    ctx.status(500);

                    //超时通知
                    for (ContextAsyncListener listener1 : listeners) {
                        try {
                            listener1.onTimeout(ctx);
                        } catch (IOException e) {
                            log.warn(e.getMessage(), e);
                        }
                    }
                } finally {
                    RunUtil.runAndTry(onTimeout);
                }
                //
            }, timeout);
        }
    }

    @Override
    public void onStart(Context ctx) {
        if (listeners != null) {
            for (ContextAsyncListener l1 : listeners) {
                try {
                    l1.onStart(ctx);
                } catch (Throwable e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    @Override
    public void onComplete(Context ctx) {
        if (timeoutFuture != null) {
            timeoutFuture.cancel(true);
        }

        if (listeners != null) {
            for (ContextAsyncListener l1 : listeners) {
                try {
                    l1.onComplete(ctx);
                } catch (Throwable e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    @Override
    public void onTimeout(Context ctx) {
        if (listeners != null) {
            for (ContextAsyncListener l1 : listeners) {
                try {
                    l1.onTimeout(ctx);
                } catch (Throwable e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    @Override
    public void onError(Context ctx, Throwable e) {
        if (listeners != null) {
            for (ContextAsyncListener l1 : listeners) {
                try {
                    l1.onError(ctx, e);
                } catch (Throwable ex) {
                    log.error(ex.getMessage(), ex);
                }
            }
        }
    }
}
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
package org.noear.solon.web.rx.integration;

import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Entity;
import org.noear.solon.core.util.KeyValues;
import org.noear.solon.rx.Completable;
import org.noear.solon.rx.handle.RxContext;
import org.noear.solon.rx.handle.RxHandler;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

/**
 * @author noear
 * @since 3.0
 */
public class RxHandlerImpl implements RxHandler {
    private static final byte[] CRLF = "\n".getBytes();

    private Flux publisher;
    private boolean isStreaming;

    public RxHandlerImpl(Publisher publisher, boolean isStreaming) {
        if (publisher instanceof Flux) {
            this.publisher = (Flux) publisher;
        } else {
            this.publisher = Flux.from(publisher);
        }

        this.isStreaming = isStreaming;
    }


    @Override
    public Completable handle(RxContext rxCtx) {
        return Completable.create(emitter -> {
            //转换上下文
            Context ctx = rxCtx.toContext();

            //如果未启动异步，则启动
            if (ctx.asyncStarted() == false) {
                ctx.asyncStart();
            }

            //开始订阅
            Flux.from(publisher).concatMap(o -> {
                        if (o == null) {
                            return Flux.empty();
                        } else if (o instanceof Entity) {
                            return getEntityBody(ctx, (Entity) o);
                        } else if (o instanceof Publisher) {
                            return Flux.from((Publisher) o);
                        } else {
                            return Flux.just(o);
                        }
                    }).doOnNext(o -> {
                        try {
                            ctx.render(o);

                            if (isStreaming) {
                                ctx.output(CRLF);
                                ctx.flush(); //流式输出，每次都要刷一下（避免缓存未输出）
                            }
                        } catch (Throwable e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .doOnError(err -> {
                        emitter.onError((Throwable) err);
                    })
                    .doOnComplete(() -> {
                        emitter.onComplete();
                    }).subscribe();
        });
    }

    private Flux<Object> getEntityBody(Context ctx, Entity entity) {
        Object data = entity.body();

        if (ctx.isHeadersSent() == false) {
            if (entity.status() > 0) {
                ctx.status(entity.status());
            }

            if (entity.headers().isEmpty() == false) {
                for (KeyValues<String> kv : entity.headers()) {
                    if (Utils.isNotEmpty(kv.getValues())) {
                        if (kv.getValues().size() > 1) {
                            //多个
                            for (String val : kv.getValues()) {
                                ctx.headerAdd(kv.getKey(), val);
                            }
                        } else {
                            //单个
                            ctx.headerSet(kv.getKey(), kv.getFirstValue());
                        }
                    }
                }
            }
        }


        if (data == null) {
            return Flux.empty();
        } else if (data instanceof Publisher) {
            return Flux.from((Publisher) data);
        } else {
            return Flux.just(data);
        }
    }
}

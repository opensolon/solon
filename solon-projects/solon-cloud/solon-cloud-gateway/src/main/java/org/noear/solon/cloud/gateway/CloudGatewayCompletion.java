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
package org.noear.solon.cloud.gateway;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import org.noear.solon.cloud.gateway.exchange.ExContext;
import org.noear.solon.core.exception.StatusException;
import org.noear.solon.core.util.KeyValues;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 响应式完成器
 *
 * @author noear
 * @since 2.9
 */
public class CloudGatewayCompletion implements Subscriber<Void> {
    static final Logger log = LoggerFactory.getLogger(CloudGatewayCompletion.class);

    private final ExContext ctx;
    private final HttpServerRequest rawRequest;

    public CloudGatewayCompletion(ExContext ctx, HttpServerRequest rawRequest) {
        this.ctx = ctx;
        this.rawRequest = rawRequest;
    }


    @Override
    public void onSubscribe(Subscription subscription) {
        subscription.request(Long.MAX_VALUE);
    }

    @Override
    public void onNext(Void unused) {
        //不管
    }

    @Override
    public void onError(Throwable throwable) {
        try {
            if (throwable instanceof StatusException) {
                StatusException status = (StatusException) throwable;
                ctx.newResponse().status(status.getCode());

                if (status.getCode() == 404) {
                    return;
                }
            } else {
                ctx.newResponse().status(500);
            }

            log.error(throwable.getMessage(), throwable);
        } finally {
            postComplete();
        }

    }

    @Override
    public void onComplete() {
        postComplete();
    }

    /**
     * 提交异步完成
     */
    public void postComplete() {
        try {
            HttpServerResponse rawResponse = rawRequest.response();

            if (rawResponse.headWritten() == false) {
                rawResponse.setStatusCode(ctx.newResponse().getStatus());

                for (KeyValues<String> kv : ctx.newResponse().getHeaders()) {
                    rawResponse.putHeader(kv.getKey(), kv.getValues());
                }
            }

            if (rawResponse.ended() == false) {
                if (ctx.newResponse().getBody() != null) {
                    rawResponse.end(ctx.newResponse().getBody());
                } else {
                    rawResponse.end();
                }
            }
        } catch (Throwable ex) {
            log.error(ex.getMessage(), ex);
        }
    }
}
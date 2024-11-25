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
package org.noear.solon.cloud.gateway.route.handler;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.*;
import io.vertx.solon.VertxHolder;
import org.noear.solon.cloud.gateway.exchange.ExBody;
import org.noear.solon.cloud.gateway.exchange.ExConstants;
import org.noear.solon.cloud.gateway.exchange.ExContext;
import org.noear.solon.cloud.gateway.exchange.ExContextImpl;
import org.noear.solon.cloud.gateway.exchange.impl.ExBodyOfBuffer;
import org.noear.solon.cloud.gateway.exchange.impl.ExBodyOfStream;
import org.noear.solon.cloud.gateway.route.RouteHandler;
import org.noear.solon.core.util.KeyValue;
import org.noear.solon.rx.Completable;
import org.noear.solon.rx.CompletableEmitter;
import org.noear.solon.core.exception.StatusException;
import org.noear.solon.core.util.KeyValues;

import java.util.Map;

/**
 * Http 路由处理器
 *
 * @author noear
 * @since 2.9
 */
public class HttpRouteHandler implements RouteHandler {
    private HttpClient httpClient;

    public HttpRouteHandler() {
        HttpClientOptions options = new HttpClientOptions()
                .setMaxPoolSize(250)
                .setConnectTimeout(1000 * 3) // milliseconds: 3s
                .setIdleTimeout(60) // seconds: 60s
                .setKeepAlive(true)
                .setKeepAliveTimeout(60); // seconds: 60s

        this.httpClient = VertxHolder.getVertx().createHttpClient(options);
    }

    @Override
    public String[] schemas() {
        return new String[]{"http", "https"};
    }

    /**
     * 处理
     */
    @Override
    public Completable handle(ExContext x) {
        try {
            ExContextImpl ctx = (ExContextImpl)x;
            ctx.rawRequest().pause();

            //构建请求
            Future<HttpClientRequest> req1 = buildHttpRequest(ctx);

            return Completable.create(emitter -> {
                req1.onComplete(ar -> {
                    if (ar.succeeded()) {
                        handleDo(ctx, ar.result(), emitter);
                    } else {
                        emitter.onError(ar.cause());
                    }
                });
            });
        } catch (Throwable ex) {
            //如查出错，说明客户端发的数据有问题
            if (ex instanceof StatusException) {
                return Completable.error(ex);
            } else {
                return Completable.error(new StatusException(ex, 400));
            }
        }
    }

    public void handleDo(ExContextImpl ctx, HttpClientRequest req1, CompletableEmitter emitter) {
        try {
            //同步 header
            for (KeyValues<String> kv : ctx.newRequest().getHeaders()) {
                if (ExConstants.Host.equals(kv.getKey())) {
                    req1.putHeader(ExConstants.X_Forwarded_Host, kv.getValues());
                } else {
                    req1.putHeader(kv.getKey(), kv.getValues());
                }
            }

            if (ctx.rawHeader(ExConstants.X_Real_IP) == null) {
                //如果上层代理没有构建 real-ip ？
                req1.putHeader(ExConstants.X_Real_IP, ctx.realIp());
            }

            ExBody exBody = ctx.newRequest().getBody();


            //同步 body（流复制）
            if (exBody instanceof ExBodyOfBuffer) {
                req1.send(((ExBodyOfBuffer) exBody).getBuffer(), ar1 -> {
                    callbackHandle(ctx, ar1, emitter);
                });
            } else {
                //使用 chunked
                req1.send(((ExBodyOfStream) exBody).getStream(), ar1 -> {
                    callbackHandle(ctx, ar1, emitter);
                });
            }

        } catch (Throwable ex) {
            //如查出错，说明客户端发的数据有问题
            if (ex instanceof StatusException) {
                emitter.onError(ex);
            } else {
                emitter.onError(new StatusException(ex, 400));
            }
        }
    }

    /**
     * 构建 http 请求对象
     */
    private Future<HttpClientRequest> buildHttpRequest(ExContext ctx) {
        RequestOptions requestOptions = new RequestOptions();

        //配置超时
        if (ctx.timeout() != null) {
            requestOptions.setConnectTimeout(ctx.timeout().getConnectTimeout() * 1000);
            requestOptions.setTimeout(ctx.timeout().getResponseTimeout() * 1000);
        }

        //配置绝对地址
        requestOptions.setAbsoluteURI(ctx.targetNew() + ctx.newRequest().getPathAndQueryString());
        requestOptions.setMethod(HttpMethod.valueOf(ctx.newRequest().getMethod()));

        return httpClient.request(requestOptions);
    }

    /**
     * 请求回调处理
     */
    private void callbackHandle(ExContextImpl ctx, AsyncResult<HttpClientResponse> ar, CompletableEmitter subscriber) {
        try {
            if (ar.succeeded()) {
                HttpClientResponse resp1 = ar.result();

                //code
                ctx.newResponse().status(resp1.statusCode());
                //header
                for (Map.Entry<String, String> kv : resp1.headers()) {
                    ctx.newResponse().headerAdd(kv.getKey(), kv.getValue());
                }

                //resp
                ctx.rawRequest().response().setStatusCode(resp1.statusCode());
                for(KeyValues<String> kv : ctx.newResponse().getHeaders()) {
                    ctx.rawRequest().response().putHeader(kv.getKey(), kv.getValues());
                }
                ctx.newResponse().getHeaders().clear();

                //body 输出（流复制） //有可能网络已关闭
                resp1.bodyHandler(buffer -> {
                    ctx.rawRequest().response().write(buffer);
                });

                resp1.end(resp1Ar -> {
                    if (resp1Ar.succeeded()) {
                        subscriber.onComplete();
                    } else {
                        subscriber.onError(resp1Ar.cause());
                    }
                });

                //ctx.newResponse().body(resp1.body());
                //subscriber.onComplete();
            } else {
                subscriber.onError(ar.cause());
            }
        } catch (Throwable ex) {
            subscriber.onError(ex);
        }
    }
}
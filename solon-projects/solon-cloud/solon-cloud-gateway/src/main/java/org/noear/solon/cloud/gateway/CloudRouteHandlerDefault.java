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

import io.vertx.core.AsyncResult;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import org.noear.solon.Solon;
import org.noear.solon.cloud.gateway.exchange.ExBody;
import org.noear.solon.cloud.gateway.exchange.ExContext;
import org.noear.solon.cloud.gateway.exchange.impl.ExBodyOfBuffer;
import org.noear.solon.cloud.gateway.exchange.impl.ExBodyOfStream;
import org.noear.solon.rx.Completable;
import org.noear.solon.rx.CompletableEmitter;
import org.noear.solon.core.LoadBalance;
import org.noear.solon.core.exception.StatusException;
import org.noear.solon.core.util.KeyValues;

import java.net.URI;
import java.util.Map;

/**
 * 分布式路由处理器默认实现
 *
 * @author noear
 * @since 2.9
 */
public class CloudRouteHandlerDefault implements CloudRouteHandler {
    private WebClient httpClient;

    public CloudRouteHandlerDefault() {
        Solon.context().getBeanAsync(Vertx.class, b -> {
            WebClientOptions options = new WebClientOptions()
                    .setMaxPoolSize(250)
                    .setConnectTimeout(1000 * 3) // milliseconds: 5s
                    .setIdleTimeout(60) // seconds: 60s
                    .setKeepAlive(true)
                    .setKeepAliveTimeout(60); // seconds: 60s

            this.httpClient = WebClient.create(b, options);
        });
    }

    /**
     * 处理
     */
    @Override
    public Completable handle(ExContext ctx) {
        try {
            //构建请求
            HttpRequest<Buffer> req1 = buildHttpRequest(ctx);

            //构建超时
            if (ctx.timeout() != null) {
                req1.connectTimeout(ctx.timeout().getConnectTimeout() * 1000);
                req1.timeout(ctx.timeout().getResponseTimeout() * 1000);
            }

            //同步 header
            for (KeyValues<String> kv : ctx.newRequest().getHeaders()) {
                req1.putHeader(kv.getKey(), kv.getValues());
            }

            ExBody exBody = ctx.newRequest().getBody();

            return Completable.create(emitter -> {
                //同步 body（流复制）
                if (exBody instanceof ExBodyOfBuffer) {
                    req1.sendBuffer(((ExBodyOfBuffer) exBody).getBuffer(), ar1 -> {
                        callbackHandle(ctx, ar1, emitter);
                    });
                } else {
                    req1.sendStream(((ExBodyOfStream) exBody).getStream(), ar1 -> {
                        callbackHandle(ctx, ar1, emitter);
                    });
                }
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

    /**
     * 构建 http 请求对象
     */
    private HttpRequest<Buffer> buildHttpRequest(ExContext ctx) {
        URI targetUri;
        if (LoadBalance.URI_SCHEME.equals(ctx.target().getScheme())) {
            String tmp = LoadBalance.get(ctx.target().getHost()).getServer(ctx.target().getPort());
            if (tmp == null) {
                throw new StatusException("The target service does not exist", 404);
            }

            targetUri = URI.create(tmp);
        } else {
            targetUri = ctx.target();
        }

        if (targetUri.getPort() > 0) {
            return httpClient.request(HttpMethod.valueOf(ctx.newRequest().getMethod()),
                    targetUri.getPort(),
                    targetUri.getHost(),
                    ctx.newRequest().getPathAndQueryString());
        } else {
            return httpClient.request(HttpMethod.valueOf(ctx.newRequest().getMethod()),
                    targetUri.getHost(),
                    ctx.newRequest().getPathAndQueryString());
        }
    }

    /**
     * 请求回调处理
     */
    private void callbackHandle(ExContext ctx, AsyncResult<HttpResponse<Buffer>> ar, CompletableEmitter subscriber) {
        try {
            if (ar.succeeded()) {
                HttpResponse<Buffer> resp1 = ar.result();

                //code
                ctx.newResponse().status(resp1.statusCode());
                //header
                for (Map.Entry<String, String> kv : resp1.headers()) {
                    ctx.newResponse().headerAdd(kv.getKey(), kv.getValue());
                }
                //body 输出（流复制） //有可能网络已关闭
                ctx.newResponse().body(resp1.body());

                subscriber.onComplete();
            } else {
                subscriber.onError(ar.cause());
            }
        } catch (Throwable ex) {
            subscriber.onError(ex);
        }
    }
}
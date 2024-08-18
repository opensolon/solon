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
import org.noear.solon.cloud.gateway.rx.ExContext;
import org.noear.solon.core.LoadBalance;
import org.noear.solon.core.exception.StatusException;
import org.noear.solon.util.KeyValues;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
                    .setConnectTimeout(1000) // milliseconds
                    .setIdleTimeout(3600) // seconds
                    .setIdleTimeoutUnit(TimeUnit.SECONDS)
                    .setKeepAlive(true)
                    .setKeepAliveTimeout(60); // seconds

            this.httpClient = WebClient.create(b, options);
        });
    }

    /**
     * 处理
     */
    @Override
    public Mono<Void> handle(ExContext ctx) {
        //构建请求
        HttpRequest<Buffer> req1 = buildHttpRequest(ctx);

        //构建超时
        if (ctx.timeout() != null) {
            req1.connectTimeout(ctx.timeout().getConnectTimeout() * 1000);
            req1.timeout(ctx.timeout().getResponseTimeout() * 1000);
        }

        try {
            //同步 header
            for (KeyValues<String> kv : ctx.newRequest().getHeaders().values()) {
                req1.putHeader(kv.getKey(), kv.getValues());
            }

            return Mono.create(monoSink -> {
                //异步 执行
                //同步 body（流复制）
                if ("GET".equals(ctx.newRequest().getMethod())) {
                    req1.send(ar -> {
                        callbackHandle(ctx, ar, monoSink);
                    });
                } else {
                    ctx.newRequest().getBody().onComplete(ar1 -> {
                        if (ar1.succeeded()) {
                            req1.sendBuffer(ar1.result(), ar2 -> {
                                callbackHandle(ctx, ar2, monoSink);
                            });
                        } else {
                            monoSink.error(new StatusException(ar1.cause(), 400));
                        }
                    });
                }
            });
        } catch (Throwable ex) {
            //如查出错，说明客户端发的数据有问题
            return Mono.error(new StatusException(ex, 400));
        }
    }

    private HttpRequest<Buffer> buildHttpRequest(ExContext ctx) {
        URI targetUri;
        if (LoadBalance.URI_SCHEME.equals(ctx.target().getScheme())) {
            targetUri = URI.create(LoadBalance.get(ctx.target().getHost()).getServer());
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

    private void callbackHandle(ExContext ctx, AsyncResult<HttpResponse<Buffer>> ar, MonoSink<Void> monoSink) {
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

            monoSink.success();
        } else {
            monoSink.error(ar.cause());
        }
    }
}
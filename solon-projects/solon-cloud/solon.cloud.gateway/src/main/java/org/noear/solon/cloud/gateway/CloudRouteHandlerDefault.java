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
import org.noear.solon.Solon;
import org.noear.solon.cloud.gateway.rx.RxContext;
import org.noear.solon.cloud.gateway.rx.RxExchange;
import org.noear.solon.core.LoadBalance;
import org.noear.solon.core.exception.StatusException;
import org.noear.solon.util.KeyValues;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;

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
            this.httpClient = WebClient.create(b);
        });
    }

    /**
     * 处理
     */
    @Override
    public Mono<Void> handle(RxContext ctx) {
        RxExchange exc = ctx.exchange();

        //构建请求
        HttpRequest<Buffer> req1 = buildHttpRequest(exc);

        //构建超时
        if (ctx.exchange().timeout() != null) {
            req1.connectTimeout(ctx.exchange().timeout().getConnectTimeout());
            req1.timeout(ctx.exchange().timeout().getResponseTimeout());
        }

        try {
            //同步 header
            for (KeyValues<String> kv : exc.request().getHeaders().values()) {
                req1.putHeader(kv.getKey(), kv.getValues());
            }

            return Mono.create(monoSink -> {
                //异步 执行
                //同步 body（流复制）
                if ("GET".equals(exc.request().getMethod())) {
                    req1.send(ar -> {
                        callbackHandle(ctx, ar, monoSink);
                    });
                } else {
                    req1.sendBuffer(exc.request().getBody(), ar -> {
                        callbackHandle(ctx, ar, monoSink);
                    });
                }
            });
        } catch (Throwable ex) {
            //如查出错，说明客户端发的数据有问题
            return Mono.error(new StatusException(ex, 400));
        }
    }

    private HttpRequest<Buffer> buildHttpRequest(RxExchange exc) {
        URI targetUri;
        if (LoadBalance.URI_SCHEME.equals(exc.target().getScheme())) {
            targetUri = URI.create(LoadBalance.get(exc.target().getHost()).getServer());
        } else {
            targetUri = exc.target();
        }

        if (targetUri.getPort() > 0) {
            return httpClient.request(HttpMethod.valueOf(exc.request().getMethod()),
                    targetUri.getPort(),
                    targetUri.getHost(),
                    exc.request().getPathAndQueryString());
        } else {
            return httpClient.request(HttpMethod.valueOf(exc.request().getMethod()),
                    targetUri.getHost(),
                    exc.request().getPathAndQueryString());
        }
    }

    private void callbackHandle(RxContext ctx, AsyncResult<HttpResponse<Buffer>> ar, MonoSink<Void> monoSink) {
        if (ar.succeeded()) {
            RxExchange ex = ctx.exchange();
            HttpResponse<Buffer> resp1 = ar.result();

            //code
            ex.response().status(resp1.statusCode());
            //header
            for (Map.Entry<String, String> kv : resp1.headers()) {
                ex.response().headerAdd(kv.getKey(), kv.getValue());
            }
            //body 输出（流复制） //有可能网络已关闭
            ex.response().body(resp1.body());

            monoSink.success();
        } else {
            monoSink.error(ar.cause());
        }
    }
}
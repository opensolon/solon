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

import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;
import org.noear.solon.cloud.gateway.exchange.ExContextImpl;
import org.noear.solon.cloud.gateway.exchange.ExContext;
import org.noear.solon.cloud.gateway.exchange.ExFilterChainImpl;
import org.noear.solon.rx.Completable;
import org.noear.solon.core.exception.StatusException;

/**
 * 分布式网关
 *
 * @author noear
 * @since 2.9
 */
public class CloudGatewayHandler implements Handler<HttpServerRequest> {
    //网关摘要
    private CloudGatewayConfiguration configuration = new CloudGatewayConfiguration();
    private Handler<HttpServerRequest> webHandler;

    public CloudGatewayHandler(Handler<HttpServerRequest> webHandler) {
        this.webHandler = webHandler;
    }

    /**
     * 处理
     */
    @Override
    public void handle(HttpServerRequest request) {
        ExContextImpl ctx = new ExContextImpl(request);
        ctx.bind(configuration.routeFind(ctx));

        if (ctx.route() == null) {
            webHandler.handle(request);
        } else {
            CloudGatewayCompletion completion = new CloudGatewayCompletion(ctx, request);

            //开始执行
            try {
                new ExFilterChainImpl(configuration.filters, this::doHandle)
                        .doFilter(ctx)
                        .subscribe(completion);

            } catch (Throwable ex) {
                //避免用户的 filter 出现异常
                //
                if (ex instanceof StatusException) {
                    StatusException se = (StatusException) ex;
                    ctx.newResponse().status(se.getCode());
                } else {
                    ctx.newResponse().status(502);
                }

                completion.postComplete();
            }
        }
    }


    /**
     * 执行处理
     */
    private Completable doHandle(ExContext ctx) {
        ExContextImpl ctx2 = (ExContextImpl) ctx;

        if (ctx2.route() == null) {
            ctx.newResponse().status(404);
            return Completable.complete();
        } else {
            try {
                return new ExFilterChainImpl(ctx2.route().getFilters(), configuration.routeHandler::handle)
                        .doFilter(ctx);
            } catch (Throwable ex) {
                //如果 buildUpstreamRequest 出错，说明请求体有问题
                if (ex instanceof StatusException) {
                    return Completable.error(ex);
                } else {
                    return Completable.error(new StatusException(ex, 400));
                }
            }
        }
    }

    /**
     * 获取配置
     */
    public CloudGatewayConfiguration getConfiguration() {
        return configuration;
    }
}
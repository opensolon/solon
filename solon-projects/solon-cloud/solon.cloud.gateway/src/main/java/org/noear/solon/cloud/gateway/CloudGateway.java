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
import org.noear.solon.cloud.gateway.route.Route;
import org.noear.solon.cloud.gateway.rx.RxContext;
import org.noear.solon.cloud.gateway.rx.RxFilterChainImpl;
import org.noear.solon.core.exception.StatusException;
import reactor.core.publisher.Mono;

/**
 * 分布式网关
 *
 * @author noear
 * @since 2.9
 */
public class CloudGateway implements Handler<HttpServerRequest> {
    //网关摘要
    private CloudGatewayConfiguration configuration = new CloudGatewayConfiguration();
    /**
     * 处理
     */
    @Override
    public void handle(HttpServerRequest request) {
        RxContext ctx = new RxContext(request);

        //开始执行
        new RxFilterChainImpl(configuration.filters, this::doHandle)
                .doFilter(ctx)
                .subscribe(new CloudGatewayCompletion(ctx));
    }


    /**
     * 执行处理
     */
    private Mono<Void> doHandle(RxContext ctx) {
        Route route = findRoute(ctx);

        if (route == null) {
            ctx.exchange().response().status(404);
            return Mono.empty();
        } else {
            try {
                return new RxFilterChainImpl(route.getFilters(), configuration.routeHandler::handle)
                        .doFilter(ctx);
            } catch (Throwable ex) {
                //如果 buildUpstreamRequest 出错，说明请求体有问题
                if (ex instanceof StatusException) {
                    return Mono.error(ex);
                } else {
                    return Mono.error(new StatusException(ex, 400));
                }
            }
        }
    }

    /**
     * 查找路由记录
     *
     * @param ctx 上下文
     */
    private Route findRoute(RxContext ctx) {
        for (Route r : configuration.routes) {
            if (r.matched(ctx)) {
                //attr +
                ctx.exchange().bind(r);
                return r;
            }
        }

        return null;
    }

    /**
     * 获取配置
     */
    public CloudGatewayConfiguration getConfiguration() {
        return configuration;
    }
}
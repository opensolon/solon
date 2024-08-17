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

import org.noear.solon.cloud.gateway.route.Route;
import org.noear.solon.cloud.gateway.route.RouteRequest;
import org.noear.solon.core.exception.StatusException;
import org.noear.solon.core.handle.*;
import org.noear.solon.web.reactive.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * 分布式网关
 *
 * @author noear
 * @since 2.9
 */
public class CloudGateway implements Handler {
    //网关摘要
    private CloudGatewayConfiguration configuration = new CloudGatewayConfiguration();

    /**
     * 处理
     */
    @Override
    public void handle(Context ctx) throws Throwable {
        //启动异步模式（-1 表示不超时）
        ctx.asyncStart(-1L, null);

        //开始执行
        new RxFilterChainImpl(configuration.filters, this::doHandle)
                .doFilter(ctx)
                .subscribe(new RxCompletion(ctx));
    }

    /**
     * 执行处理
     */
    private Mono<Void> doHandle(Context ctx) {
        Route route = findRoute(ctx);

        if (route == null) {
            ctx.status(404);
            return Mono.empty();
        } else {
            try {
                buildUpstreamRequest(ctx);

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

    private RouteRequest buildUpstreamRequest(Context ctx) throws Throwable {
        RouteRequest request = new RouteRequest();

        request.method(ctx.method());
        request.queryString(ctx.queryString());
        request.path(ctx.pathNew());
        for (Map.Entry<String, List<String>> kv : ctx.headersMap().entrySet()) {
            request.header(kv.getKey(), kv.getValue());
        }
        request.body(ctx.bodyAsStream(), ctx.contentType());

        //attr +
        ctx.attrSet(RouteRequest.ATTR_NAME, request);

        return request;
    }

    /**
     * 查找路由记录
     *
     * @param ctx 上下文
     */
    private Route findRoute(Context ctx) {
        for (Route r : configuration.routes) {
            if (r.matched(ctx)) {
                //attr +
                ctx.attrSet(Route.ATTR_NAME, r);
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
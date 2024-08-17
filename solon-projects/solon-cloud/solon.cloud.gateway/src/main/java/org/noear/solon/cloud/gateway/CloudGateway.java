package org.noear.solon.cloud.gateway;

import org.noear.solon.cloud.gateway.route.Route;
import org.noear.solon.core.handle.*;
import org.noear.solon.web.reactive.*;
import reactor.core.publisher.Mono;

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
            //记录路由
            ctx.attrSet(Route.ATTR_NAME, route);

            return new RxFilterChainImpl(route.getFilters(), configuration.routeHandler::handle)
                    .doFilter(ctx);
        }
    }

    /**
     * 查找路由记录
     *
     * @param ctx 上下文
     */
    private Route findRoute(Context ctx) {
        for (Route r : configuration.routes) {
            if (r.matched(ctx)) {
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
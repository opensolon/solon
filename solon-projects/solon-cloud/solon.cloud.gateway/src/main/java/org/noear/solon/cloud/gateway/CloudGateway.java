package org.noear.solon.cloud.gateway;

import org.noear.solon.core.exception.StatusException;
import org.noear.solon.core.handle.*;
import org.noear.solon.core.util.RankEntity;
import org.noear.solon.web.reactive.*;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

/**
 * 分布式网关
 *
 * @author noear
 * @since 2.9
 */
public class CloudGateway implements Handler {
    //路由记录
    protected List<CloudRoute> routes = new ArrayList<>();
    //过滤器
    protected List<RankEntity<RxFilter>> filters = new ArrayList<>();

    /**
     * 处理
     */
    @Override
    public void handle(Context ctx) throws Throwable {
        //启动异步模式（-1 表示不超时）
        ctx.asyncStart(-1L, null);

        //开始执行
        new RxFilterChainImpl(filters, this::doHandle)
                .doFilter(ctx)
                .subscribe(new RxCompletion(ctx));
    }

    /**
     * 执行处理
     */
    protected Mono<Void> doHandle(Context ctx) {
        CloudRoute routing = findRoute(ctx);

        if (routing == null) {
            return Mono.error(new StatusException("Not Found", 404));
        } else {
            RxHandler routeHandler = createRouteHandler(routing);
            return new RxFilterChainImpl(routing.getFilters(), routeHandler::handle)
                    .doFilter(ctx);
        }
    }

    /**
     * 创建路由处理器
     *
     * @param routing 路由记录
     */
    protected RxHandler createRouteHandler(CloudRoute routing) {
        return new CloudRouteHandlerDefault(routing);
    }


    /**
     * 查找路由记录
     *
     * @param ctx 上下文
     */
    protected CloudRoute findRoute(Context ctx) {
        for (CloudRoute r : routes) {
            if (r.matched(ctx)) {
                return r;
            }
        }

        return null;
    }

    /**
     * 添加过滤器
     */
    public void filter(RxFilter filter) {
        filter(filter, 0);
    }

    /**
     * 添加过滤器
     */
    public void filter(RxFilter filter, int index) {
        this.filters.add(new RankEntity<>(filter::doFilter, index));
        this.filters.sort(Comparator.comparingInt(e -> e.index));
    }

    /**
     * 登记路由
     */
    public void route(CloudRoute routing) {
        routes.add(routing);
    }

    /**
     * 登记路由
     */
    public void route(String id, Consumer<CloudRoute> builder) {
        CloudRoute routing = new CloudRoute();
        routing.id(id);
        builder.accept(routing);

        route(routing);
    }
}
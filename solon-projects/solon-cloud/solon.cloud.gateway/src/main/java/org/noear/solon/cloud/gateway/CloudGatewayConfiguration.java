package org.noear.solon.cloud.gateway;

import org.noear.solon.core.util.RankEntity;
import org.noear.solon.web.reactive.RxFilter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

/**
 * 分布式网关配置
 *
 * @author noear
 * @since 2.9
 */
public class CloudGatewayConfiguration {
    //路由记录
    protected List<CloudRoute> routes = new ArrayList<>();
    //路由处理
    protected CloudRouteHandler routeHandler = new CloudRouteHandlerSimple();
    //过滤器
    protected List<RankEntity<RxFilter>> filters = new ArrayList<>();

    /**
     * 配置过滤器
     *
     * @param filter 过滤器
     */
    public CloudGatewayConfiguration filter(RxFilter filter) {
        return filter(filter, 0);
    }

    /**
     * 配置过滤器
     *
     * @param filter 过滤器
     * @param index  顺序位
     */
    public CloudGatewayConfiguration filter(RxFilter filter, int index) {
        if (filter != null) {
            this.filters.add(new RankEntity<>(filter::doFilter, index));
            this.filters.sort(Comparator.comparingInt(e -> e.index));
        }

        return this;
    }

    /**
     * 配置路由
     *
     * @param id      标识
     * @param builder 路由构建器
     */
    public CloudGatewayConfiguration route(String id, Consumer<CloudRoute> builder) {
        CloudRoute route = new CloudRoute();
        route.id(id);
        builder.accept(route);

        return route(route);
    }

    /**
     * 配置路由
     *
     * @param route 路由
     */
    public CloudGatewayConfiguration route(CloudRoute route) {
        if (route != null) {
            routes.add(route);
        }

        return this;
    }

    /**
     * 配置路由处理
     *
     * @param routeHandler 路由处理器
     */
    public CloudGatewayConfiguration routeHandler(CloudRouteHandler routeHandler) {
        this.routeHandler = routeHandler;
        return this;
    }
}
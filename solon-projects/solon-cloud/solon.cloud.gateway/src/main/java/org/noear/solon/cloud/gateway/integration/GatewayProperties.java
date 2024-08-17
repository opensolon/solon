package org.noear.solon.cloud.gateway.integration;

import org.noear.solon.cloud.gateway.CloudRouteHandler;
import org.noear.solon.web.reactive.RxFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * 分布式网关配置属性
 *
 * @author noear
 * @since 2.9
 */
public class GatewayProperties {
    /**
     * 路由记录
     */
    private List<RouteProperties> routes = new ArrayList<>();
    /**
     * 路由处理
     */
    private CloudRouteHandler routeHandler;
    /**
     * 过滤器
     */
    private List<RxFilter> filters = new ArrayList<>();

    /**
     * 路由记录
     */
    public List<RouteProperties> getRoutes() {
        return routes;
    }

    /**
     * 路由处理
     */
    public CloudRouteHandler getRouteHandler() {
        return routeHandler;
    }

    /**
     * 过滤器
     */
    public List<RxFilter> getFilters() {
        return filters;
    }
}
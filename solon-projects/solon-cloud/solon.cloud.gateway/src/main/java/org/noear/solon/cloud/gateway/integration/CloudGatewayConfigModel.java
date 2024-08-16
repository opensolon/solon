package org.noear.solon.cloud.gateway.integration;

import org.noear.solon.web.reactive.RxFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * 分布式网关配置模型
 *
 * @author noear
 * @since 2.9
 */
public class CloudGatewayConfigModel {
    private List<CloudRouteConfigModel> routes = new ArrayList<>();
    private List<RxFilter> filters = new ArrayList<>();

    /**
     * 过滤器
     */
    public List<RxFilter> getFilters() {
        return filters;
    }

    /**
     * 路由记录
     */
    public List<CloudRouteConfigModel> getRoutes() {
        return routes;
    }
}

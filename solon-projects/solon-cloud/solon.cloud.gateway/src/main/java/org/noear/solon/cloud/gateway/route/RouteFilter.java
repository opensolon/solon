package org.noear.solon.cloud.gateway.route;

import org.noear.solon.web.reactive.RxFilter;

/**
 * 路由过滤器
 *
 * @author noear
 * @since 2.9
 */
@FunctionalInterface
public interface RouteFilter extends RxFilter {
    /**
     * 初始化
     *
     * @param config 配置
     */
    default void init(String config) {

    }
}

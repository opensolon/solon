package org.noear.solon.cloud.gateway.route;

import org.noear.solon.core.handle.Context;

import java.util.function.Predicate;

/**
 * 路由断定器
 *
 * @author noear
 * @since 2.9
 */
@FunctionalInterface
public interface RoutePredicate extends Predicate<Context> {
    /**
     * 初始化
     *
     * @param config 配置
     */
    default void init(String config) {

    }
}

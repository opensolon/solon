package org.noear.solon.cloud.gateway;

import org.noear.solon.cloud.gateway.rx.RxFilter;

/**
 * 分布式网关过滤器
 *
 * @author noear
 * @since 2.9
 */
@FunctionalInterface
public interface CloudGatewayFilter extends RxFilter {
}

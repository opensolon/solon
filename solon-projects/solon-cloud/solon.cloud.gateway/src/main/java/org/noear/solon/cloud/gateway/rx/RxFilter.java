package org.noear.solon.cloud.gateway.rx;

import reactor.core.publisher.Mono;

/**
 * 响应式过滤器
 *
 * @author noear
 * @since 2.9
 */
@FunctionalInterface
public interface RxFilter {
    /**
     * 过滤
     *
     * @param ctx 上下文
     */
    Mono<Void> doFilter(RxContext ctx, RxFilterChain chain);
}

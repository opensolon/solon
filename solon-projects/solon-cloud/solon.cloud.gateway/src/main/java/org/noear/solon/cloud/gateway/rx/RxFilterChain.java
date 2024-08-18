package org.noear.solon.cloud.gateway.rx;

import reactor.core.publisher.Mono;

/**
 * 响应式过滤器链
 *
 * @author noear
 * @since 2.9
 */
@FunctionalInterface
public interface RxFilterChain {
    /**
     * 过滤
     *
     * @param ctx 上下文
     */
    Mono<Void> doFilter(ExContext ctx);
}

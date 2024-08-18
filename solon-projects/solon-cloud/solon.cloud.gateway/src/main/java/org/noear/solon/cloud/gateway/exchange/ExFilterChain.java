package org.noear.solon.cloud.gateway.exchange;

import reactor.core.publisher.Mono;

/**
 * 交换过滤器链
 *
 * @author noear
 * @since 2.9
 */
@FunctionalInterface
public interface ExFilterChain {
    /**
     * 过滤
     *
     * @param ctx 上下文
     */
    Mono<Void> doFilter(ExContext ctx);
}

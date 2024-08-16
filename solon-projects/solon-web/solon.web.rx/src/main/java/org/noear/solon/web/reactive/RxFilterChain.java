package org.noear.solon.web.reactive;

import org.noear.solon.core.handle.Context;
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
    Mono<Void> doFilter(Context ctx);
}

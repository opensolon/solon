package org.noear.solon.cloud.gateway.rx;

import reactor.core.publisher.Mono;

/**
 * 响应式处理器
 *
 * @author noear
 * @since 2.9
 */
@FunctionalInterface
public interface RxHandler {
    Mono<Void> handle(ExContext ctx);
}

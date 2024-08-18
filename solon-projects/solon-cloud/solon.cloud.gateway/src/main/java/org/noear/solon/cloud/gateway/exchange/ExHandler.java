package org.noear.solon.cloud.gateway.exchange;

import reactor.core.publisher.Mono;

/**
 * 交换处理器
 *
 * @author noear
 * @since 2.9
 */
@FunctionalInterface
public interface ExHandler {
    Mono<Void> handle(ExContext ctx);
}

package org.noear.solon.web.reactive;

import org.noear.solon.core.handle.Context;
import reactor.core.publisher.Mono;

/**
 * 响应式处理器
 *
 * @author noear
 * @since 2.9
 */
public interface RxHandler {
    Mono<Void> handle(Context ctx);
}

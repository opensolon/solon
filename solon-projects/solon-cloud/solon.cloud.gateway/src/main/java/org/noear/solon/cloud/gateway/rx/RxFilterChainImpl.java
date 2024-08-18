package org.noear.solon.cloud.gateway.rx;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.util.RankEntity;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 响应式过滤器链实现
 *
 * @author noear
 * @since 2.9
 */
public class RxFilterChainImpl implements RxFilterChain {
    private final List<RankEntity<RxFilter>> filterList;
    private final RxHandler lastHandler;
    private int index;

    public RxFilterChainImpl(List<RankEntity<RxFilter>> filterList) {
        this.filterList = filterList;
        this.index = 0;
        this.lastHandler = null;
    }

    public RxFilterChainImpl(List<RankEntity<RxFilter>> filterList, RxHandler lastHandler) {
        this.filterList = filterList;
        this.index = 0;
        this.lastHandler = lastHandler;
    }

    @Override
    public Mono<Void> doFilter(RxContext ctx) {
        if (lastHandler == null) {
            return filterList.get(index++).target.doFilter(ctx, this);
        } else {
            if (index < filterList.size()) {
                return filterList.get(index++).target.doFilter(ctx, this);
            } else {
                return lastHandler.handle(ctx);
            }
        }
    }
}
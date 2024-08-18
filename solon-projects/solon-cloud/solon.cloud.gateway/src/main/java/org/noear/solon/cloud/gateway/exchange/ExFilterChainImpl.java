package org.noear.solon.cloud.gateway.exchange;

import org.noear.solon.core.util.RankEntity;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 交换过滤器链实现
 *
 * @author noear
 * @since 2.9
 */
public class ExFilterChainImpl implements ExFilterChain {
    private final List<RankEntity<ExFilter>> filterList;
    private final ExHandler lastHandler;
    private int index;

    public ExFilterChainImpl(List<RankEntity<ExFilter>> filterList) {
        this.filterList = filterList;
        this.index = 0;
        this.lastHandler = null;
    }

    public ExFilterChainImpl(List<RankEntity<ExFilter>> filterList, ExHandler lastHandler) {
        this.filterList = filterList;
        this.index = 0;
        this.lastHandler = lastHandler;
    }

    @Override
    public Mono<Void> doFilter(ExContext ctx) {
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
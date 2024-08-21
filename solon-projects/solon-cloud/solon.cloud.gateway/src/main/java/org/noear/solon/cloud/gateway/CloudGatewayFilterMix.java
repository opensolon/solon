package org.noear.solon.cloud.gateway;

import org.noear.solon.cloud.gateway.exchange.ExContext;
import org.noear.solon.cloud.gateway.exchange.ExFilter;
import org.noear.solon.cloud.gateway.exchange.ExFilterChain;
import org.noear.solon.cloud.gateway.exchange.ExFilterChainImpl;
import org.noear.solon.cloud.gateway.route.RouteFactoryManager;
import org.noear.solon.core.util.RankEntity;
import org.noear.solon.rx.Completable;

import java.util.ArrayList;
import java.util.List;

/**
 * 分布式网关过滤器组合器
 *
 * @author noear
 * @since 2.9
 */
public abstract class CloudGatewayFilterMix implements CloudGatewayFilter {
    private List<RankEntity<ExFilter>> filters = new ArrayList<>();

    public CloudGatewayFilterMix() {
        this.register();
    }

    /**
     * 登记（组合过滤器）
     */
    public abstract void register();

    public void filter(String filterConfig) {
        ExFilter filter = RouteFactoryManager.buildFilter(filterConfig);
        if (filter != null) {
            throw new IllegalArgumentException("ExFilter config wrong: " + filterConfig);
        }

        filters.add(new RankEntity<>(filter, filters.size()));
    }

    @Override
    public Completable doFilter(ExContext ctx, ExFilterChain chain) {
        return new ExFilterChainImpl(filters, c -> doFilterDo(c, chain))
                .doFilter(ctx);
    }

    /**
     * 执行过滤
     */
    public Completable doFilterDo(ExContext ctx, ExFilterChain chain) {
        return chain.doFilter(ctx);
    }
}

package org.noear.solon.cloud.gateway.filter;

import org.noear.solon.cloud.gateway.route.UpstreamRequest;
import org.noear.solon.core.handle.Context;
import org.noear.solon.web.reactive.RxFilter;
import org.noear.solon.web.reactive.RxFilterChain;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

/**
 * @author noear
 * @since 2.9
 */
public class StripPrefixFilter implements RxFilter {
    private int parts;

    public StripPrefixFilter(String config) {
        this.parts = Integer.parseInt(config);
    }

    @Override
    public Mono<Void> doFilter(Context ctx, RxFilterChain chain) {
        UpstreamRequest request = UpstreamRequest.of(ctx);

        //目标路径重组
        List<String> pathFragments = Arrays.asList(request.getPath().split("/", -1));
        String newPath = "/" + String.join("/", pathFragments.subList(parts + 1, pathFragments.size()));
        request.path(newPath);

        return chain.doFilter(ctx);
    }
}

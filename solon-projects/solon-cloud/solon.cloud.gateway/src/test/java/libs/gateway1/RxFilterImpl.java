package libs.gateway1;

import org.noear.solon.core.handle.Context;
import org.noear.solon.web.reactive.RxFilter;
import org.noear.solon.web.reactive.RxFilterChain;
import reactor.core.publisher.Mono;

public class RxFilterImpl implements RxFilter {
    @Override
    public Mono<Void> doFilter(Context ctx, RxFilterChain chain) {
        System.out.println("RxFilterImpl.doFilter");

        return chain.doFilter(ctx);
    }
}

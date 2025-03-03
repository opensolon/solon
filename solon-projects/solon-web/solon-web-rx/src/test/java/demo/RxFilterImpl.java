package demo;

import org.noear.solon.annotation.Component;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Filter;
import org.noear.solon.core.handle.FilterChain;
import org.noear.solon.rx.Completable;
import org.noear.solon.rx.handle.RxContext;
import org.noear.solon.rx.handle.RxFilter;
import org.noear.solon.rx.handle.RxFilterChain;

/**
 * @author noear 2025/2/16 created
 */
@Component
public class RxFilterImpl implements Filter, RxFilter {
    @Override
    public void doFilter(Context ctx, FilterChain chain) throws Throwable {
        try {
            chain.doFilter(ctx);
        } catch (Throwable ex) {
            System.out.println("RxFilterImpl.doFilter error");
        }
    }

    @Override
    public Completable doFilter(RxContext ctx, RxFilterChain chain) {
        return chain.doFilter(ctx)
                .doOnComplete(() -> {
                    System.out.println("RxFilterImpl.doFilter called");
                }).doOnError(err -> {
                    System.out.println("RxFilterImpl.doFilter error");
                });
    }
}

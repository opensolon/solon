package features.context1;

import org.noear.solon.Solon;
import org.noear.solon.annotation.Component;
import org.noear.solon.cloud.gateway.CloudGatewayFilter;
import org.noear.solon.cloud.gateway.exchange.ExContext;
import org.noear.solon.cloud.gateway.exchange.ExFilterChain;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ContextHolder;
import org.noear.solon.core.handle.Result;
import org.noear.solon.rx.Completable;

/**
 * @author noear 2024/12/18 created
 */
@Component
public class App implements CloudGatewayFilter {
    public static void main(String[] args) {
        Solon.start(App.class, new String[]{"-cfg=context1.yml"}, app -> {

        });
    }

    @Override
    public Completable doFilter(ExContext ctx, ExFilterChain chain) {
        try {
            Context ctx2 = ctx.toContext();
            ContextHolder.currentSet(ctx2);
            filterBef(ctx2);

            if (ctx2.getRendered() || ctx2.attr("output") != null) {
                ctx2.flush();
                return Completable.complete();
            }
        } catch (Throwable ex) {
            return Completable.error(ex);
        } finally {
            ContextHolder.currentRemove();
        }

        return chain.doFilter(ctx);
    }

    private void filterBef(Context ctx) throws Throwable {
        int s1 = ctx.paramAsInt("s", 0);

        if (s1 == 1) {
            throw new RuntimeException("s1");
        } else if (s1 == 2) {
            ctx.render(Result.failure());
        } else if (s1 == 3) {
            ctx.output("err");
        }
    }
}

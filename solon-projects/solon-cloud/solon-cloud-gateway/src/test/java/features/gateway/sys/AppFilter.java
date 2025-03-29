package features.gateway.sys;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import org.noear.solon.annotation.Component;
import org.noear.solon.cloud.gateway.CloudGatewayFilter;
import org.noear.solon.cloud.gateway.exchange.ExContext;
import org.noear.solon.cloud.gateway.exchange.ExFilterChain;
import org.noear.solon.rx.Completable;

/**
 * @author noear 2025/3/29 created
 */
@Component
public class AppFilter implements CloudGatewayFilter {
    @Override
    public Completable doFilter(ExContext ctx, ExFilterChain chain) {
        ctx.rawBody().andThen(new Handler<AsyncResult<Buffer>>() {
            @Override
            public void handle(AsyncResult<Buffer> bufferAsyncResult) {
                if (bufferAsyncResult.succeeded()) {
                    String temp = bufferAsyncResult.result().toString();
                    System.out.println("---------::" + temp);
                } else {
                    System.out.println("---------::err::" + bufferAsyncResult.cause().getMessage());
                }
            }
        });

        return chain.doFilter(ctx);
    }
}

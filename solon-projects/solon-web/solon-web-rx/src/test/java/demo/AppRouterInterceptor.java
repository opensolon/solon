package demo;

import org.noear.solon.annotation.Component;
import org.noear.solon.boot.web.MimeType;
import org.noear.solon.core.handle.Action;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.core.route.RouterInterceptor;
import org.noear.solon.core.route.RouterInterceptorChain;
import reactor.core.publisher.Flux;

/**
 * @author noear 2024/8/30 created
 */
//@Component
public class AppRouterInterceptor implements RouterInterceptor {
    @Override
    public void doIntercept(Context ctx, Handler mainHandler, RouterInterceptorChain chain) throws Throwable {
        //根据返回类型，构建 contentType
        Action action = (Action) mainHandler;
        if (action != null && action.method().getReturnType() == Flux.class) {
            ctx.contentType(MimeType.APPLICATION_X_NDJSON_VALUE);
        }

        chain.doIntercept(ctx, mainHandler);
    }
}

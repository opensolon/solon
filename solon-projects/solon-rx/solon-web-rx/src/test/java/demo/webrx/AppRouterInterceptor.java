package demo.webrx;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.noear.solon.annotation.Managed;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.core.route.RouterInterceptor;
import org.noear.solon.core.route.RouterInterceptorChain;
import org.noear.solon.core.util.MimeType;

/**
 * @author noear 2024/8/30 created
 */
@Managed
public class AppRouterInterceptor implements RouterInterceptor {
    @Override
    public void doIntercept(Context ctx, Handler mainHandler, RouterInterceptorChain chain) throws Throwable {
        chain.doIntercept(ctx, mainHandler);
    }

    @Override
    public Object postResult(Context ctx, Object result) throws Throwable {
        //根据返回类型，构建 contentType
        if (result instanceof Multi) {
            if (MimeType.isStreaming(ctx.acceptNew()) == false) {
                return ((Multi) result).collect().asList().toMulti();
            }
        } else if (result instanceof Uni) {
            return ((Uni) result).toMulti();
        }

        return result;
    }
}

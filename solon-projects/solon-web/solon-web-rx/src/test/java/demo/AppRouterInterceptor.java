package demo;

import org.noear.solon.annotation.Component;
import org.noear.solon.boot.web.MimeType;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.core.route.RouterInterceptor;
import org.noear.solon.core.route.RouterInterceptorChain;
import org.noear.solon.rx.Mama;

/**
 * @author noear 2024/8/30 created
 */
@Component
public class AppRouterInterceptor implements RouterInterceptor {
    @Override
    public void doIntercept(Context ctx, Handler mainHandler, RouterInterceptorChain chain) throws Throwable {
        chain.doIntercept(ctx, mainHandler);
    }

    @Override
    public Object postResult(Context ctx, Object result) throws Throwable {
        //根据返回类型，构建 contentType
        if (result instanceof Mama && ctx.acceptNew() != null) {
            if (ctx.acceptNew().startsWith(MimeType.APPLICATION_X_NDJSON_VALUE) == false) {
                return ((Mama) result).collectList();
            }
        }

        return result;
    }
}

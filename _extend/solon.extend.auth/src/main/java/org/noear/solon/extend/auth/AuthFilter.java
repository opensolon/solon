package org.noear.solon.extend.auth;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Filter;
import org.noear.solon.core.handle.FilterChain;
import org.noear.solon.core.handle.Result;
import org.noear.solon.extend.validation.ValidatorManager;

/**
 * @author noear
 * @since 1.4
 */
public class AuthFilter implements Filter {
    @Override
    public void doFilter(Context ctx, FilterChain chain) throws Throwable {
        if (AuthServiceProxy.getInstance().verifyPath(ctx.path(), ctx.method())) {
            chain.doFilter(ctx);
        } else {
            Result result = Result.failure(401, "Unauthorized");
            ValidatorManager.global().failureDo(ctx, null, result, result.getDescription());
        }
    }
}

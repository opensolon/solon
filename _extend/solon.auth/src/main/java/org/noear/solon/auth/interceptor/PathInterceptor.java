package org.noear.solon.auth.interceptor;

import org.noear.solon.auth.AuthUtil;
import org.noear.solon.auth.annotation.AuthPath;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;

/**
 * AuthPath 注解拦截器
 *
 * @author noear
 * @since 1.4
 */
public class PathInterceptor extends AbstractInterceptor<AuthPath> {
    @Override
    public Class<AuthPath> type() {
        return AuthPath.class;
    }

    @Override
    public Result verify(AuthPath anno) throws Exception {
        Context ctx = Context.current();

        if (ctx == null) {
            return Result.succeed();
        } else {
            if (AuthUtil.verifyPath(ctx.pathNew(), ctx.method())) {
                return Result.succeed();
            } else {
                return Result.failure(403, AuthUtil.MESSAGE_OF_PATH);
            }
        }
    }
}

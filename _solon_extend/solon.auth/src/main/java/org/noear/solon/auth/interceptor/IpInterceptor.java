package org.noear.solon.auth.interceptor;

import org.noear.solon.auth.AuthStatus;
import org.noear.solon.auth.AuthUtil;
import org.noear.solon.auth.annotation.AuthIp;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;

/**
 * AuthIp 注解拦截器
 *
 * @author noear
 * @since 1.4
 */
public class IpInterceptor extends AbstractInterceptor<AuthIp> {
    @Override
    public Class<AuthIp> type() {
        return AuthIp.class;
    }

    @Override
    public Result verify(AuthIp anno) throws Exception {
        Context ctx = Context.current();

        if (ctx == null) {
            return Result.succeed();
        } else {
            String ip = ctx.realIp();

            if (AuthUtil.verifyIp(ip)) {
                return Result.succeed();
            } else {
                return AuthStatus.OF_IP.toResult(ip);
            }
        }
    }
}

package org.noear.solon.extend.auth.interceptor;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.extend.auth.AuthUtil;
import org.noear.solon.extend.auth.annotation.AuthIp;

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
                return Result.failure(403, ip + AuthUtil.MESSAGE_OF_IP);
            }
        }
    }
}

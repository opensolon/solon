package org.noear.solon.web.sso;

import org.noear.solon.core.handle.Context;
import org.noear.solon.validation.annotation.Logined;
import org.noear.solon.validation.annotation.LoginedChecker;

/**
 * 单点登录检测器
 *
 * @author noear
 * @since 2.2
 */
public class SsoLoginedChecker implements LoginedChecker {
    @Override
    public boolean check(Logined anno, Context ctx, String userKeyName) {
        return SsoUtil.isLogined(ctx);
    }
}

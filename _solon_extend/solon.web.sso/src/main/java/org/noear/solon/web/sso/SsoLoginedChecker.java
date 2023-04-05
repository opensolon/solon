package org.noear.solon.web.sso;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
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

    private SsoService ssoService;

    public SsoLoginedChecker(SsoService ssoService) {
        this.ssoService = ssoService;
    }

    @Override
    public boolean check(Logined anno, Context ctx, String userKeyName) {
        //获取会话中的用户Id
        long userId = ctx.sessionAsLong(SsoService.SESSION_USER_ID);
        //获取会话中的用户SsoKey
        String userSsoKey = ctx.session(SsoService.SESSION_SSO_KEY, "");

        if (userId == 0) {
            return false;
        }

        //为单测增加支持（不然没法跑）
        if (Solon.cfg().isDebugMode()) {
            return true;
        }

        if (Utils.isEmpty(userSsoKey)) {
            return false;
        }

        String userSsoKeyOfUser = ssoService.getUserSsoKey(userId);
        return userSsoKey.equals(userSsoKeyOfUser);
    }
}

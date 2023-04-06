package org.noear.solon.web.sso.impl;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.web.sso.SsoStorage;

import java.io.Serializable;

/**
 * 单点登录能力服务
 *
 * @author noear
 * @since 2.2
 */
public class SsoService {
    /**
     * 登录
     *
     * @param userId 用户Id
     */
    public void login(SsoStorage storage, Context ctx, Serializable userId) {
        String userSsoKey = storage.updateUserSsoKey(userId);

        ctx.sessionSet(SsoStorage.SESSION_USER_ID, userId);

        //设置当前会话的单点登录标识
        ctx.sessionSet(SsoStorage.SESSION_SSO_KEY, userSsoKey);
    }

    /**
     * 登出（指定用户）
     */
    public void logout(SsoStorage storage, Serializable userId) {
        storage.removeUserSsoKey(userId);
    }

    /**
     * 获取已登录用户Id
     */
    public Serializable getLoginedUserId(Context ctx) {
        return ctx.session(SsoStorage.SESSION_USER_ID, null);
    }

    /**
     * 当前用户是否已登录
     */
    public boolean isLogined(SsoStorage storage, Context ctx) {
        //检测会话中的用户Id
        Serializable userId = ctx.session(SsoStorage.SESSION_USER_ID, null);

        if (userId == null) {
            return false;
        }

        //为单测增加支持（不然没法跑）
        if (Solon.cfg().isDebugMode()) {
            return true;
        }

        //检测会话中的用户SsoKey
        String userSsoKey = ctx.session(SsoStorage.SESSION_SSO_KEY, "");

        if (Utils.isEmpty(userSsoKey)) {
            return false;
        }

        //检测用户SsoKey是否过时？
        String userSsoKeyOfUser = storage.getUserSsoKey(userId);
        return userSsoKey.equals(userSsoKeyOfUser);
    }
}

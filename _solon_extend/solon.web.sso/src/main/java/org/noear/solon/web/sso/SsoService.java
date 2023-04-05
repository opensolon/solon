package org.noear.solon.web.sso;

import org.noear.solon.core.handle.Context;

/**
 * 单点登录数据服务
 *
 * @author noear
 * @since 2.2
 */
public interface SsoService {
    String SESSION_USER_ID = "user_id";
    String SESSION_SSO_KEY = "user_sso_key";

    /**
     * 更新单点登录标识
     */
    void updateUserSsoKey(Context ctx, long userId);

    /**
     * 获取单点登录标识
     */
    String getUserSsoKey(long userId);
}

package org.noear.solon.web.sso;

import java.io.Serializable;

/**
 * 单点登录存储服务
 *
 * @author noear
 * @since 2.2
 */
public interface SsoStorage {
    String SESSION_USER_ID = "user_id";
    String SESSION_SSO_KEY = "user_sso_key";

    /**
     * 更新单点登录标识
     */
    String updateUserSsoKey(Serializable userId);

    /**
     * 移除单点登录标识
     * */
    void removeUserSsoKey(Serializable userId);

    /**
     * 获取单点登录标识
     */
    String getUserSsoKey(Serializable userId);
}

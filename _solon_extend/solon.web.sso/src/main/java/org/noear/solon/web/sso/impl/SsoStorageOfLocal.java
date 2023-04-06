package org.noear.solon.web.sso.impl;

import org.noear.solon.Utils;
import org.noear.solon.web.sso.SsoStorage;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 单点登录数据服务 Local 实现
 *
 * @author noear
 * @since 2.2
 */
public class SsoStorageOfLocal implements SsoStorage {
    private Map<String, String> storage = new HashMap<>();

    /**
     * 更新单点登录标识
     */
    @Override
    public String updateUserSsoKey(Serializable userId) {
        String storageKey = SsoStorage.SESSION_SSO_KEY + ":" + userId;
        String userSsoKey = Utils.guid();

        //设置用户的单点登录标识（当前会话 与 用户的'单点登录标识'，说明当前会话不是最新的登录）
        storage.put(storageKey, userSsoKey);

        return userSsoKey;
    }

    /**
     * 移除单点登录标识
     * */
    @Override
    public void removeUserSsoKey(Serializable userId) {
        String storageKey = SsoStorage.SESSION_SSO_KEY + ":" + userId;

        storage.remove(storageKey);
    }

    /**
     * 获取单点登录标识
     */
    @Override
    public String getUserSsoKey(Serializable userId) {
        String storageKey = SsoStorage.SESSION_SSO_KEY + ":" + userId;

        return storage.get(storageKey);
    }
}
